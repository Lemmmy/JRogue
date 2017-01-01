package pw.lemmmy.jrogue.dungeon.entities.player;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.TriConsumer;
import org.json.JSONObject;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.Prompt;
import pw.lemmmy.jrogue.dungeon.entities.*;
import pw.lemmmy.jrogue.dungeon.entities.actions.*;
import pw.lemmmy.jrogue.dungeon.entities.containers.Container;
import pw.lemmmy.jrogue.dungeon.entities.containers.EntityItem;
import pw.lemmmy.jrogue.dungeon.entities.effects.InjuredFoot;
import pw.lemmmy.jrogue.dungeon.entities.effects.StrainedLeg;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.AStarPathfinder;
import pw.lemmmy.jrogue.dungeon.entities.player.roles.Role;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.entities.skills.SkillLevel;
import pw.lemmmy.jrogue.dungeon.items.*;
import pw.lemmmy.jrogue.dungeon.items.comestibles.ItemComestible;
import pw.lemmmy.jrogue.dungeon.items.magical.spells.Spell;
import pw.lemmmy.jrogue.dungeon.items.projectiles.ItemProjectile;
import pw.lemmmy.jrogue.dungeon.items.quaffable.ItemQuaffable;
import pw.lemmmy.jrogue.dungeon.items.quaffable.potions.ItemPotion;
import pw.lemmmy.jrogue.dungeon.items.valuables.ItemGold;
import pw.lemmmy.jrogue.dungeon.items.weapons.ItemProjectileLauncher;
import pw.lemmmy.jrogue.dungeon.items.weapons.ItemWeapon;
import pw.lemmmy.jrogue.dungeon.items.weapons.ItemWeaponMelee;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.states.TileStateClimbable;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.Path;
import pw.lemmmy.jrogue.utils.RandomUtils;
import pw.lemmmy.jrogue.utils.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Player extends LivingEntity {
	private AStarPathfinder pathfinder = new AStarPathfinder();
	
	private String name;
	private Role role;
	
	private int energy;
	private int maxEnergy;
	private int chargingTurns = 0;
	private Map<Character, Spell> knownSpells;
	
	private int nutrition;
	private NutritionState lastNutritionState;
	
	private int spendableSkillPoints = 3;
	private Attributes attributes = new Attributes();
	private Map<Skill, SkillLevel> skills;
	
	private int gold = 0;
	
	private boolean godmode = false;
	
	public Player(Dungeon dungeon, Level level, int x, int y) { // unserialisation constructor
		super(dungeon, level, x, y);
	}
	
	public Player(Dungeon dungeon, Level level, int x, int y, String name, Role role) {
		super(dungeon, level, x, y, 1);
		
		this.name = name;
		this.role = role;
		
		nutrition = 1000;
		maxHealth = getMaxHealth();
		
		energy = maxEnergy = role.getMaxEnergy();
		knownSpells = new HashMap<>(role.getStartingSpells());
		
		role.assignAttributes(attributes);
		
		setInventoryContainer(new Container("Inventory"));
		skills = new HashMap<>(role.getStartingSkills());
		
		if (getContainer().isPresent()) {
			role.getStartingItems().forEach(i -> {
				Optional<Container.ContainerEntry> entry = getContainer().get().add(i);

				if (entry.isPresent()) {
					if (role.getStartingLeftHand() == entry.get().getStack()) {
						setLeftHand(entry.get());
					}

					if (role.getStartingRightHand() == entry.get().getStack()) {
						setRightHand(entry.get());
					}
				}
			});
		}
		
		setHealth(getMaxHealth());
		setMovementPoints(Dungeon.NORMAL_SPEED);
	}
	
	@Override
	public int getMaxHealth() {
		return 10 + getConstitutionBonus();
	}
	
	public int getConstitutionBonus() {
		return attributes != null ? (int) Math.floor(0.25 * attributes.getAttribute(Attribute.CONSTITUTION) - 2) : 0;
	}
	
	@Override
	public int getHealingRate() {
		int constitution = attributes.getAttribute(Attribute.CONSTITUTION);

		switch (getNutritionState()) {
			case FAINTING:
				return 100 - constitution;
			case STARVING:
				return 40 - constitution / 3;
			default:
				return 20 - constitution / 2;
		}
	}
	
	public int getEnergy() {
		return energy;
	}
	
	public int getMaxEnergy() {
		return maxEnergy;
	}
	
	public int getChargingRate() {
		return (int) Math.floor((38 - getExperienceLevel()) * (3.5f / 6f));
	}
	
	public void charge(int amount) {
		energy = Math.min(maxEnergy, energy + amount);
	}
	
	private void levelUpEnergy() {
		int wisdom = attributes.getAttribute(Attribute.WISDOM);
		int gainMax = wisdom / 2 + 2;
		int gain = RandomUtils.roll(gainMax) + 2;
		
		maxEnergy += gain;
		charge(gain);
	}
	
	public Map<Character, Spell> getKnownSpells() {
		return knownSpells;
	}
	
	@Override
	public int getMovementSpeed() {
		int speed = Dungeon.NORMAL_SPEED;
		
		if (hasStatusEffect(InjuredFoot.class)) {
			speed -= 1;
		}
		
		if (hasStatusEffect(StrainedLeg.class)) {
			speed -= 1;
		}
		
		return speed;
	}
	
	@Override
	public int getDamageModifier(DamageSource damageSource, int damage) {
		return godmode ? 0 : super.getDamageModifier(damageSource, damage);
	}
	
	@Override
	public int getBaseArmourClass() {
		return 10;
	}
	
	@Override
	public int getDepth() {
		return 10;
	}
	
	@Override
	public Size getSize() {
		return LivingEntity.Size.LARGE;
	}
	
	public Role getRole() {
		return role;
	}
	
	public Attributes getAttributes() {
		return attributes;
	}
	
	public int getNutrition() {
		return nutrition;
	}
	
	public void setNutrition(int nutrition) {
		this.nutrition = nutrition;
	}
	
	public NutritionState getNutritionState() {
		if (nutrition >= 1500) {
			return NutritionState.CHOKING;
		} else if (nutrition >= 1000) {
			return NutritionState.STUFFED;
		} else if (nutrition >= 600) {
			return NutritionState.NOT_HUNGRY;
		} else if (nutrition >= 300) {
			return NutritionState.HUNGRY;
		} else if (nutrition >= 0) {
			return NutritionState.STARVING;
		} else {
			return NutritionState.FAINTING;
		}
	}
	
	public int getSpendableSkillPoints() {
		return spendableSkillPoints;
	}
	
	public void decrementSpendableSkillPoints() {
		spendableSkillPoints = Math.max(0, spendableSkillPoints - 1);
	}
	
	@Override
	public String getName(boolean requiresCapitalisation) {
		return requiresCapitalisation ? StringUtils.capitalize(name) : name;
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_PLAYER;
	}
	
	public void giveGold(int amount) {
		gold += amount;
	}
	
	public int getGold() {
		return gold;
	}
	
	public boolean canTakeGold(int amount) {
		return gold > amount;
	}
	
	public void takeGold(int amount) {
		gold = Math.max(0, gold - amount);
	}
	
	public int getVisibilityRange() {
		return 10 * ((getLightLevel() - 20) / 100) + 10;
	}
	
	public int getLightLevel() {
		return getLevel().getTile(getX(), getY()).getLightIntensity();
	}
	
	public int getCorridorVisibilityRange() {
		return 2 * ((getLightLevel() - 20) / 100) + 5;
	}
	
	public SkillLevel getSkillLevel(Skill skill) {
		return !skills.containsKey(skill) ? SkillLevel.UNSKILLED : skills.get(skill);
	}
	
	public boolean isDebugger() {
		return name.equalsIgnoreCase("debugger");
	}
	
	public void godmode() {
		this.godmode = true;
	}
	
	@Override
	public void applyMovementPoints() {
		setMovementPoints(getMovementPoints() + getMovementSpeed());
	}
	
	@Override
	public void onLevelUp() {
		levelUpEnergy();
		
		getDungeon().greenYou("levelled up! You are now experience level %,d.", getExperienceLevel());
		getDungeon().greenYou("have %,d spendable skill point%s.", ++spendableSkillPoints, spendableSkillPoints == 1 ? "" : "s");
	}
	
	@Override
	public void update() {
		super.update();
		
		updateEnergy();
		updateNutrition();
		
		if (godmode) {
			setHealth(getMaxHealth());
		}
	}
	
	private void updateEnergy() {
		energy = Math.max(0, Math.min(maxEnergy, energy));
		
		if (energy < maxEnergy) {
			chargingTurns++;
		}
		
		if (chargingTurns >= getChargingRate()) {
			int wisdom = attributes.getAttribute(Attribute.WISDOM);
			int intelligence = attributes.getAttribute(Attribute.INTELLIGENCE);
			
			int chargeMax = (int) Math.floor((wisdom + intelligence) / 15) + 1;
			int chargeAmount = RandomUtils.roll(chargeMax);
			
			charge(chargeAmount);
			
			chargingTurns = 0;
		}
	}
	
	private void updateNutrition() {
		if (getNutritionState() != lastNutritionState) {
			lastNutritionState = getNutritionState();
			
			switch (getNutritionState()) {
				case CHOKING: 	getDungeon().redYou("are choking!"); break;
				case HUNGRY: 	getDungeon().orangeYou("are starting to feel hungry."); break;
				case STARVING: 	getDungeon().redYou("are starving!"); break;
				case FAINTING: 	getDungeon().redYou("are passing out due to starvation!"); break;
				default:		break;
			}
		}
		
		if (getNutritionState() == NutritionState.CHOKING) {
			damage(DamageSource.CHOKING, 1, this, true);
		}
		
		nutrition--;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("name", name);
		obj.put("role", role.getClass().getName());
		obj.put("spendableSkillPoints", getSpendableSkillPoints());
		obj.put("energy", getEnergy());
		obj.put("maxEnergy", getMaxEnergy());
		obj.put("chargingTurns", chargingTurns);
		obj.put("nutrition", getNutrition());
		obj.put("gold", getGold());
		obj.put("godmode", godmode);
		
		attributes.serialise(obj);
		
		JSONObject serialisedSkills = new JSONObject();
		skills.forEach((skill, skillLevel) -> serialisedSkills.put(skill.name(), skillLevel.name()));
		obj.put("skills", serialisedSkills);
		
		JSONObject serialisedSpells = new JSONObject();
		knownSpells.forEach((spellLetter, spell) -> {
			JSONObject serialisedSpell = new JSONObject();
			spell.serialise(serialisedSpell);
			serialisedSpells.put(spellLetter.toString() + "!" + spell.getClass().getName(), serialisedSpell);
		});
		obj.put("knownSpells", serialisedSpells);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void unserialise(JSONObject obj) {
		setInventoryContainer(new Container("Inventory"));
		skills = new HashMap<>();
		knownSpells = new HashMap<>();
		
		super.unserialise(obj);
		
		name = obj.getString("name");
		spendableSkillPoints = obj.getInt("spendableSkillPoints");
		energy = obj.getInt("energy");
		maxEnergy = obj.getInt("maxEnergy");
		chargingTurns = obj.getInt("chargingTurns");
		nutrition = obj.getInt("nutrition");
		gold = obj.getInt("gold");
		godmode = obj.getBoolean("godmode");
		
		attributes = new Attributes();
		attributes.unserialise(obj);
		
		String roleClassName = obj.getString("role");
		try {
			Class<? extends Role> roleClass = (Class<? extends Role>) Class.forName(roleClassName);
			Constructor<? extends Role> roleConstructor = roleClass.getConstructor();
			role = roleConstructor.newInstance();
		} catch (ClassNotFoundException e) {
			JRogue.getLogger().error("Unknown role class {}", roleClassName);
		} catch (NoSuchMethodException e) {
			JRogue.getLogger().error("Role class {} has no unserialisation constructor", roleClassName);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			JRogue.getLogger().error("Error loading role class {}", roleClassName);
			JRogue.getLogger().error(e);
		}
		
		JSONObject serialisedSkills = obj.getJSONObject("skills");
		serialisedSkills.keySet().forEach(k -> {
			String v = serialisedSkills.getString(k);
			skills.put(Skill.valueOf(k), SkillLevel.valueOf(v));
		});
		
		JSONObject serialisedSpells = obj.getJSONObject("knownSpells");
		serialisedSpells.keySet().forEach(key -> {
			Character spellLetter = key.charAt(0);
			String spellClassName = key.substring(2, key.length());
			
			try {
				Class<? extends Spell> spellClass = (Class<? extends Spell>) Class.forName(spellClassName);
				Constructor<? extends Spell> spellConstructor = spellClass.getConstructor();
				Spell spell = spellConstructor.newInstance();
				spell.unserialise(serialisedSpells.getJSONObject(key));
				knownSpells.put(spellLetter, spell);
			} catch (ClassNotFoundException e) {
				JRogue.getLogger().error("Unknown spell class {}", spellClassName);
			} catch (NoSuchMethodException e) {
				JRogue.getLogger().error("Spell class {} has no unserialisation constructor", spellClassName);
			} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
				JRogue.getLogger().error("Error loading spell class {}", spellClassName);
				JRogue.getLogger().error(e);
			}
		});
	}
	
	@Override
	protected void onDamage(DamageSource damageSource, int damage, LivingEntity attacker, boolean isPlayer) {}
	
	@Override
	protected void onDie(DamageSource damageSource, int damage, LivingEntity attacker, boolean isPlayer) {
		if (damageSource.getDeathString() != null) {
			getDungeon().log("[RED]" + damageSource.getDeathString() + "[]");
		} else {
			getDungeon().redYou("die");
		}
		
		getDungeon().deleteSave();
	}
	
	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {
		getDungeon().orangeYou("step on your own foot.");
	}
	
	@Override
	protected void onWalk(LivingEntity walker, boolean isPlayer) {}
	
	@Override
	public boolean canBeWalkedOn() {
		return false;
	}
	
	public void teleport(int x, int y) {
		setAction(new ActionTeleport(x, y, new EntityAction.NoCallback()));
		getDungeon().turn();
	}
	
	public void walk(int dx, int dy) {
		dx = Math.max(-1, Math.min(1, dx));
		dy = Math.max(-1, Math.min(1, dy));
		
		int newX = getX() + dx;
		int newY = getY() + dy;
		
		Tile tile = getLevel().getTile(newX, newY);
		
		// TODO: More in-depth movement verification
		// 		 e.g. a player shouldn't be able to travel diagonally to
		//       a different tile type than the one they are standing on
		//       unless it is a door
		
		if (tile == null) {
			getDungeon().log("It would be silly to walk there.");
			return;
		}
		
		List<Entity> destEntities = getLevel().getEntitiesAt(newX, newY);
		
		if (destEntities.size() > 0) {
			// TODO: Ask the player to confirm if they want to attack something silly (e.g. their familiar or a clerk)
			
			Optional<Entity> ent = destEntities.stream()
				.filter(e -> e instanceof LivingEntity)
				.findFirst();
			
			if (ent.isPresent()) {
				if (getRightHand() != null && getRightHand().getStack().getItem() instanceof ItemWeaponMelee) {
					((ItemWeaponMelee) getRightHand().getStack().getItem()).hit(this, (LivingEntity) ent.get());
				} else if (getLeftHand() != null && getLeftHand().getStack().getItem() instanceof ItemWeaponMelee) {
					((ItemWeaponMelee) getLeftHand().getStack().getItem()).hit(this, (LivingEntity) ent.get());
				} else {
					getDungeon().You("have no weapon equipped!"); // TODO: Make it possible to attack bare-handed
				}
			} else {
				walkAction(tile, newX, newY);
			}
		} else {
			walkAction(tile, newX, newY);
		} // TODO: Restructure this mess
		
		getDungeon().turn();
	}
	
	private void walkAction(Tile tile, int x, int y) {
		if (tile.getType().getSolidity() != TileType.Solidity.SOLID) {
			setAction(new ActionMove(x, y, new EntityAction.NoCallback()));
		} else if (tile.getType() == TileType.TILE_ROOM_DOOR_LOCKED) {
			getDungeon().The("door is locked.");
		} else if (tile.getType() == TileType.TILE_ROOM_DOOR_CLOSED) {
			tile.setType(TileType.TILE_ROOM_DOOR_OPEN);
			getDungeon().You("open the door.");
		}
	}
	
	public void travelDirectional() {
		getDungeon().prompt(new Prompt("Travel in what direction?", null, true, new Prompt.SimplePromptCallback(getDungeon()) {
			@Override
			public void onResponse(char response) {
				if (!Utils.MOVEMENT_CHARS.containsKey(response)) {
					getDungeon().log(String.format("Invalid direction '[YELLOW]%s[]'.", response));
					return;
				}
				
				Path pathTaken = new Path();
				
				Integer[] d = Utils.MOVEMENT_CHARS.get(response);
				int dx = d[0];
				int dy = d[1];
				
				for (int i = 0; i < 50; i++) { // max 50 steps in one move
					Tile destTile = getLevel().getTile(getX() + dx, getY() + dy);
					
					if (
						destTile == null ||
							i >= 1 && destTile.getType().getSolidity() == TileType.Solidity.WALK_THROUGH ||
							destTile.getType().getSolidity() == TileType.Solidity.SOLID
						) {
						break;
					}
					
					int oldX = getX();
					int oldY = getY();
					
					pathTaken.addStep(destTile);
					setAction(new ActionMove(getX() + dx, getY() + dy, new EntityAction.NoCallback()));
					getDungeon().turn();
					
					if (oldX == getX() && oldY == getY()) { // we didn't go anywhere, so stop
						break;
					}
					
					if (i > 2 && getLevel().getAdjacentMonsters(getX(), getY()).size() > 0) {
						break;
					}
				}
				
				getDungeon().showPath(pathTaken);
			}
		}));
	}
	
	public void travelPathfind(int tx, int ty) {
		Tile destTile = getLevel().getTile(tx, ty);
		
		if (destTile == null || !getLevel().isTileDiscovered(tx, ty)) {
			getDungeon().You("can't travel there.");
			return;
		}
		
		Path path = pathfinder.findPath(
			getLevel(),
			getX(),
			getY(),
			tx,
			ty,
			50,
			true,
			new ArrayList<>()
		);
		
		Path pathTaken = new Path();
		
		if (path == null || path.getLength() == 0) {
			getDungeon().You("can't travel there.");
			return;
		}
		
		AtomicBoolean stop = new AtomicBoolean(false);
		AtomicInteger i = new AtomicInteger(0);
		
		path.forEach(step -> {
			i.incrementAndGet();
			
			if (stop.get()) { return; }
			if (getX() == step.getX() && getY() == step.getY()) { return; }
			
			if (step.getType().getSolidity() == TileType.Solidity.SOLID) {
				stop.set(true);
				return;
			}
			
			int oldX = getX();
			int oldY = getY();
			
			pathTaken.addStep(step);
			setAction(new ActionMove(step.getX(), step.getY(), new EntityAction.NoCallback()));
			getDungeon().turn();
			
			if (oldX == getX() && oldY == getY()) {
				stop.set(true);
				return;
			}
			
			if (i.get() > 2 && getLevel().getAdjacentMonsters(getX(), getY()).size() > 0) {
				stop.set(true);
			}
		});
		
		getDungeon().showPath(pathTaken);
	}
	
	public void kick() {
		getDungeon().prompt(new Prompt("Kick in what direction?", null, true, new Prompt.SimplePromptCallback(getDungeon()) {
			@Override
			public void onResponse(char response) {
				if (!Utils.MOVEMENT_CHARS.containsKey(response)) {
					getDungeon().log(String.format("Invalid direction '[YELLOW]%s[]'.", response));
					return;
				}
				
				int wisdom = attributes.getAttribute(Attribute.WISDOM);

				if (wisdom > 5) {
					if (hasStatusEffect(InjuredFoot.class)) {
						getDungeon().Your("foot is in no shape for kicking."); return;
					}

					if (hasStatusEffect(StrainedLeg.class)) {
						getDungeon().Your("leg is in no shape for kicking."); return;
					}
				}
				
				Integer[] d = Utils.MOVEMENT_CHARS.get(response);
				int dx = d[0];
				int dy = d[1];
				
				if (getLevel().getEntitiesAt(getX() + dx, getY() + dy).size() > 0) {
					setAction(new ActionKick(d, getLevel().getEntitiesAt(getX() + dx, getY() + dy).get(0), new EntityAction.NoCallback()));
				} else {
					setAction(new ActionKick(d, new EntityAction.NoCallback()));
				}
				
				getDungeon().turn();
			}
		}));
	}
	
	public void castSpell(Spell spell) {
		switch (spell.getDirectionType()) {
			case NON_DIRECTIONAL:
				castSpellNonDirectional(spell);
				break;
			default:
				castSpellDirectional(spell);
				break;
		}
	}
	
	private boolean canCastSpell(Spell spell) {
		return energy >= spell.getCastingCost();
	}
	
	private void castSpellNonDirectional(Spell spell) {
		if (!canCastSpell(spell)) {
			getDungeon().redYou("don't have enough energy to cast that spell.");
			return;
		}
		
		nutrition -= spell.getNutritionCost();
		energy -= spell.getCastingCost();
		spell.castNonDirectional(this);
		getDungeon().turn();
	}
	
	private void castSpellDirectional(Spell spell) {
		if (!canCastSpell(spell)) {
			getDungeon().redYou("don't have enough energy to cast that spell.");
			return;
		}
		
		getDungeon().prompt(new Prompt("Cast in what direction?", null, true, new Prompt.SimplePromptCallback(getDungeon()) {
			@Override
			public void onResponse(char response) {
				if (!Utils.MOVEMENT_CHARS.containsKey(response) &&
					spell.canCastAtSelf() && response != '5' && response != '.') {
					getDungeon().log(String.format("Invalid direction '[YELLOW]%s[]'.", response));
					return;
				}
				
				Integer[] d = response == '5' || response == '.' ?
							  new Integer[] {0, 0} :
							  Utils.MOVEMENT_CHARS.get(response);
				int dx = d[0];
				int dy = d[1];
				
				nutrition -= spell.getNutritionCost();
				energy -= spell.getCastingCost();
				spell.castDirectional(Player.this, dx, dy);
				getDungeon().turn();
			}
		}));
	}
	
	public enum InventoryUseResult {
		SUCCESS, NO_CONTAINER, NO_ITEM
	}

	public InventoryUseResult useInventoryItem(String promptString, Predicate<ItemStack> isEligible, TriConsumer<Character, Container.ContainerEntry, Container> responseCallback, boolean allowHyphen) {
		if (!getContainer().isPresent()) {
			return InventoryUseResult.NO_CONTAINER;
		}

		Container inv = getContainer().get();
		Map<Character, ItemStack> eligibleItems = inv.getItems().entrySet().stream()
			.filter(e -> isEligible.test(e.getValue()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		if (eligibleItems.isEmpty()) {
			return InventoryUseResult.NO_ITEM;
		}

		char[] options = ArrayUtils.toPrimitive(eligibleItems.keySet().toArray(new Character[0]));
		options = Arrays.copyOf(options, options.length + 1);
		options[options.length - 1] = '-';

		getDungeon().prompt(new Prompt(promptString, options, true, new Prompt.SimplePromptCallback(getDungeon()) {
			@Override
			public void onResponse(char response) {
				Optional<Container.ContainerEntry> containerEntry = inv.get(response);

				if (!allowHyphen && !containerEntry.isPresent()) {
					getDungeon().log("Invalid item '[YELLOW]%s[]'.", response);
					return;
				}

				Container.ContainerEntry entry = containerEntry.isPresent() ? containerEntry.get() : null;
				responseCallback.accept(response, entry, inv);
			}
		}));

		return InventoryUseResult.SUCCESS;
	}

	public InventoryUseResult useInventoryItem(String promptString, Predicate<ItemStack> isEligible, TriConsumer<Character, Container.ContainerEntry, Container> responseCallback) {
		return useInventoryItem(promptString, isEligible, responseCallback, false);
	}

	public void eat() {
		List<Entity> floorEntities = getLevel().getEntitiesAt(getX(), getY());
		
		Optional<Entity> floorFood = floorEntities.stream()
			/* health and safety note: floor food is dangerous */
			.filter(e -> e instanceof EntityItem)
			.filter(e -> ((EntityItem) e).getItem() instanceof ItemComestible)
			.findFirst();
		
		if (floorFood.isPresent()) {
			eatFromFloor((EntityItem) floorFood.get());
		} else {
			eatFromInventory();
		}
	}
	
	private void eatFromFloor(EntityItem entity) {
		ItemStack stack = entity.getItemStack();
		ItemComestible item = (ItemComestible) entity.getItem();

		String itemName = item.getName(false, false);
		String article = item.beginsWithVowel() ? "an" : "a";
		String promptString = item.isis() ? String.format("There is [YELLOW]%s[] here. Eat it?", itemName) :
											String.format("There is %s [YELLOW]%s[] here. Eat it?", article, itemName);

		getDungeon().prompt(new Prompt(promptString, new char[]{'y', 'n'}, true, new Prompt.SimplePromptCallback(getDungeon()) {
			@Override
			public void onResponse(char response) {
				if (response == 'n') {
					eatFromInventory();
					return;
				}

				ItemComestible itemCopy = (ItemComestible) item.copy();

				setAction(new ActionEat(
					itemCopy,
					(EntityAction.CompleteCallback) ent -> {
						if (stack.getCount() == 1) {
							entity.getLevel().removeEntity(entity);
						} else {
							stack.subtractCount(1);
						}

						if (itemCopy.getEatenState() != ItemComestible.EatenState.EATEN) {
							EntityItem newStack = new EntityItem(
								getDungeon(),
								getLevel(),
								getX(),
								getY(),
								new ItemStack(itemCopy, 1)
							);

							getLevel().addEntity(newStack);
						}
					}
				));

				getDungeon().turn();
			}
		}));
	}
	
	private void eatFromInventory() {
		InventoryUseResult result = useInventoryItem("Eat what?", is -> is.getItem() instanceof ItemComestible, (c, ce, inv) -> {
			ItemStack stack = ce.getStack();
			ItemComestible item = (ItemComestible) stack.getItem();
			ItemComestible itemCopy = (ItemComestible) item.copy();

			setAction(new ActionEat(
				itemCopy,
				(EntityAction.CompleteCallback) entity -> {
					if (stack.getCount() == 1) {
						inv.remove(ce.getLetter());
					} else {
						stack.subtractCount(1);
					}

					if (itemCopy.getEatenState() != ItemComestible.EatenState.EATEN) {
						inv.add(new ItemStack(itemCopy, 1));
					}
				}
			));

			getDungeon().turn();
		});

		switch (result) {
			case NO_CONTAINER:
			case NO_ITEM: 		getDungeon().yellowYou("have nothing to eat."); break;
			default: 			break;
		}
	}
	
	public void quaff() {
		InventoryUseResult result =
				useInventoryItem("Quaff what?", is -> is.getItem() instanceof ItemQuaffable && ((ItemQuaffable)is.getItem()).canQuaff(), (c, ce, inv) -> {
			ItemStack stack = ce.getStack();
			ItemQuaffable quaffable = (ItemQuaffable) stack.getItem();

			setAction(new ActionQuaff(
				quaffable,
				(EntityAction.CompleteCallback) entity -> {
					if (stack.getCount() == 1) {
						inv.remove(ce.getLetter());
					} else {
						stack.subtractCount(1);
					}

					if (quaffable instanceof ItemPotion) {
						ItemPotion potion = (ItemPotion) quaffable;

						ItemPotion emptyPotion = new ItemPotion();
						emptyPotion.setPotionType(potion.getPotionType());
						emptyPotion.setBottleType(potion.getBottleType());
						emptyPotion.setPotionColour(potion.getPotionColour());
						emptyPotion.setEmpty(true);
						inv.add(new ItemStack(emptyPotion, 1));
					}
				})
			);
		});

		switch (result) {
			case NO_CONTAINER:
			case NO_ITEM:		getDungeon().yellowYou("have nothing to quaff."); break;
			default:			break;
		}
	}
	
	public void consume(ItemComestible item) {
		if (item.getTurnsRequiredToEat() == 1) {
			getDungeon().greenYou("eat the %s.", item.getName(false, false));
			nutrition += item.getNutrition();
			
			item.eatPart();
			return;
		}
		
		if (item.getEatenState() != ItemComestible.EatenState.EATEN) {
			if (item.getTurnsEaten() == item.getTurnsRequiredToEat() - 1) {
				getDungeon().greenYou("finish eating the %s.", item.getName(false, false));
				
				nutrition += Math.ceil(item.getNutrition() / item.getTurnsRequiredToEat());
				
				if (item.getStatusEffects(this) != null) {
					item.getStatusEffects(this).forEach(this::addStatusEffect);
				}
			} else {
				getDungeon().You("eat a part of the %s.", item.getName(false, false));
				
				nutrition += Math.floor(item.getNutrition() / item.getTurnsRequiredToEat());
				
				if (item.getStatusEffects(this) != null &&
					getNutritionState() != NutritionState.STARVING &&
					getNutritionState() != NutritionState.FAINTING &&
					attributes.getAttribute(Attribute.WISDOM) > 6) {
					
					getDungeon().You("feel funny - it might not be a good idea to continue eating.");
				}
			}
		}
		
		item.eatPart();
	}
	
	public void pickup() {
		List<Entity> floorEntities = getLevel().getEntitiesAt(getX(), getY());
		
		for (Entity entity : floorEntities) {
			if (entity instanceof EntityItem) { // TODO: Prompt if there are multiple items
				ItemStack stack = ((EntityItem) entity).getItemStack();
				Item item = stack.getItem();
				
				if (item instanceof ItemGold) {
					giveGold(stack.getCount());
					getLevel().removeEntity(entity);
					getDungeon().turn();
					getDungeon().You("pick up [YELLOW]%s[].", stack.getName(false));
				} else if (getContainer().isPresent()) {
					Optional<Container.ContainerEntry> result = getContainer().get().add(stack);
					
					if (!result.isPresent()) {
						getDungeon().You("can't hold any more items.");
						return;
					}
					
					getLevel().removeEntity(entity);
					getDungeon().turn();
					
					if (item.isis() || stack.getCount() > 1) {
						getDungeon().You(
							"pick up [YELLOW]%s[] ([YELLOW]%s[]).",
							stack.getName(false),
							result.get().getLetter()
						);
					} else {
						getDungeon().You(
							"pick up %s [YELLOW]%s[] ([YELLOW]%s[]).",
							stack.beginsWithVowel() ? "an" : "a", stack.getName(false), result.get().getLetter()
						);
					}
					
					break;
				} else {
					getDungeon().yellowYou("can't hold anything!");
				}
			}
		}
	}
	
	public void drop() {
		InventoryUseResult result = useInventoryItem("Drop what?", is -> true, (c, ce, inv) -> {
			ItemStack stack = ce.getStack();
			Item item = stack.getItem();

			inv.remove(c);
			dropItem(stack);

			if (item.isis() || stack.getCount() > 1) {
				getDungeon().You("drop [YELLOW]%s[] ([YELLOW]%s[]).", stack.getName(false), c);
			} else {
				getDungeon().You("drop %s [YELLOW]%s[] ([YELLOW]%s[]).", stack.beginsWithVowel() ? "an" : "a", stack.getName(false), c);
			}

			getDungeon().turn();
		});

		switch (result) {
			case NO_CONTAINER:	getDungeon().yellowYou("can't hold anything!"); break;
			case NO_ITEM:		getDungeon().yellowYou("don't have any items to drop!"); break;
			default: 			break;
		}
	}
	
	public void loot() {
		List<Entity> containerEntities = getLevel().getEntitiesAt(getX(), getY()).stream()
			.filter(e -> !(e instanceof Player) && e.getContainer().isPresent())
			.collect(Collectors.toList());
		
		if (containerEntities.size() == 0) {
			getDungeon().log("There is nothing to loot here.");
			return;
		}
		
		getDungeon().turn();
		
		Entity containerEntity = containerEntities.get(0);
		
		if (!containerEntity.lootable()) {
			containerEntity.lootFailedString().ifPresent(s -> getDungeon().log(s));
			return;
		}
		
		containerEntity.lootSuccessString().ifPresent(s -> getDungeon().log(s));
		getDungeon().showContainer(containerEntity);
	}
	
	public void wield() {
		InventoryUseResult result = useInventoryItem("Wield what?", is -> is.getItem() instanceof Wieldable, (c, ce, inv) -> {
			if (c == '-') {
				setLeftHand(null);
				setRightHand(null);
				getDungeon().You("unwield everything.");
				getDungeon().turn();
				return;
			}

			if (ce == null) {
				getDungeon().log(String.format("Invalid item '[YELLOW]%s[]'.", c));
				return;
			}

			ItemStack stack = ce.getStack();
			Item item = stack.getItem();

			if (getRightHand() != null && ((Wieldable) getRightHand().getStack().getItem()).isTwoHanded()) {
				setLeftHand(null);
			}

			setRightHand(ce);

			if (((Wieldable) item).isTwoHanded()) {
				setLeftHand(ce);
			}

			String name = stack.getName(false);

			if (item.isis() || stack.getCount() > 1) {
				getDungeon().You("wield [YELLOW]%s[] ([YELLOW]%s[]).", name, c);
			} else {
				getDungeon().You("wield %s [YELLOW]%s[] ([YELLOW]%s[]).", stack.beginsWithVowel() ? "an" : "a", name, c);
			}

			getDungeon().turn();
		}, true);

		switch (result) {
			case NO_CONTAINER:
			case NO_ITEM:		getDungeon().yellowYou("have nothing to wield!"); break;
			default:			break;
		}
	}
	
	public void fire() {
		// TODO: quiver
	}
	
	public void throwItem() {
		InventoryUseResult result = useInventoryItem("Throw what?", is -> true, (c, ce, inv) -> {
			ItemStack stack = ce.getStack();
			Item item = stack.getItem();
			
			getDungeon().prompt(new Prompt("In what direction?", null, true, new Prompt.SimplePromptCallback(getDungeon()) {
				@Override
				public void onResponse(char response) {
					if (!Utils.MOVEMENT_CHARS.containsKey(response)) {
						getDungeon().log(String.format("Invalid direction '[YELLOW]%s[]'.", response));
						return;
					}
					
					Integer[] d = Utils.MOVEMENT_CHARS.get(response);
					int dx = d[0];
					int dy = d[1];
					
					if (
						item instanceof ItemProjectile &&
						getRightHand() != null &&
						getRightHand().getStack().getItem() instanceof ItemProjectileLauncher
					) {
						ItemProjectileLauncher launcher = (ItemProjectileLauncher) getRightHand().getStack().getItem();
						boolean fired = launcher.fire(Player.this, (ItemProjectile) item, dx, dy);
						
						if (fired) {
							if (stack.getCount() <= 1) {
								inv.remove(ce.getLetter());
							} else {
								stack.subtractCount(1);
							}
						}
					} else {
						// TODO: regular item throwing
					}
					
					getDungeon().turn();
				}
			}));
		});
		
		switch (result) {
			case NO_CONTAINER:	getDungeon().yellowYou("can't hold anything!"); break;
			case NO_ITEM:		getDungeon().yellowYou("don't have any items to throw!"); break;
			default: 			break;
		}
	}
	
	public void climbAny() {
		Tile tile = getLevel().getTile(getX(), getY());
		
		if (tile.getType() != TileType.TILE_ROOM_STAIRS_UP && tile.getType() != TileType.TILE_ROOM_LADDER_UP &&
			tile.getType() != TileType.TILE_ROOM_STAIRS_DOWN && tile.getType() != TileType.TILE_ROOM_LADDER_DOWN) {
			getDungeon().log("[YELLOW]There is nothing to climb here.[]");
			return;
		}
		
		boolean up = tile.getType() == TileType.TILE_ROOM_STAIRS_UP || tile.getType() == TileType.TILE_ROOM_LADDER_UP;
		climb(tile, up);
	}
	
	public void climbUp() {
		Tile tile = getLevel().getTile(getX(), getY());
		
		if (tile.getType() != TileType.TILE_ROOM_STAIRS_UP && tile.getType() != TileType.TILE_ROOM_LADDER_UP) {
			getDungeon().log("[YELLOW]There is nothing to climb up here.[]");
			return;
		}
		
		climb(tile, true);
	}
	
	public void climbDown() {
		Tile tile = getLevel().getTile(getX(), getY());
		
		if (tile.getType() != TileType.TILE_ROOM_STAIRS_DOWN && tile.getType() != TileType.TILE_ROOM_LADDER_DOWN) {
			getDungeon().log("[YELLOW]There is nothing to climb down here.[]");
			return;
		}
		
		climb(tile, false);
	}
	
	private void climb(Tile tile, boolean up) {
		if (!tile.hasState() || !(tile.getState() instanceof TileStateClimbable)) {
			return;
		}
		
		TileStateClimbable tsc = (TileStateClimbable) tile.getState();
		
		if (!tsc.getLinkedLevel().isPresent()) {
			int depth = getLevel().getDepth() + (up ? 1 : -1);
			Level level = getDungeon().newLevel(depth, tile);
			level.processEntityQueues();
			tsc.setLinkedLevelUUID(level.getUUID());
			tsc.setDestPosition(level.getSpawnX(), level.getSpawnY());
		}
		
		if (tsc.getLinkedLevel().isPresent()) {
			Level level = tsc.getLinkedLevel().get();
			getDungeon().changeLevel(level, tsc.getDestX(), tsc.getDestY());
		}
	}
	
	public Hit hitFromMonster(DamageSource damageSource, int damage, LivingEntity attacker) {
		int target;
		
		if (getArmourClass() >= 0) {
			target = 10 + getArmourClass() + attacker.getExperienceLevel();
		} else {
			int targetRange = RandomUtils.random(getArmourClass(), -1);
			target = 10 + targetRange + attacker.getExperienceLevel();
			
			damage -= RandomUtils.roll(Math.abs(getArmourClass()));
		}
		
		if (target <= 0) {
			target = 1;
		}
		
		int roll = RandomUtils.jroll(20);
		
		if (target <= 1 && roll <= 1) {
			return new Hit(HitType.JUST_MISS, damage);
		} else if (roll >= target) {
			return new Hit(HitType.MISS, damage);
		} else {
			return new Hit(HitType.SUCCESS, damage);
		}
	}
	
	private int getStrengthHitBonus() {
		int strength = getAttributes().getAttribute(Attribute.STRENGTH);
		return (int) Math.floor(strength / 5) - 2;
	}
	
	private int getDexterityHitBonus() {
		int dexterity = getAttributes().getAttribute(Attribute.DEXTERITY);
		return dexterity < 15 ? (int) Math.floor(dexterity / 5) - 3 : dexterity - 15;
	}
	
	private int getWeaponSkillHitBonus(SkillLevel skillLevel) {
		switch (skillLevel) {
			case UNSKILLED:	return -4;
			case ADVANCED: 	return 2;
			case EXPERT:
			case MASTER:	return 3;
			default:		return 0;
		}
	}
	
	private int getTwoHandedHitBonus(SkillLevel skillLevel) {
		switch (skillLevel) {
			case UNSKILLED:	return -9;
			case ADVANCED:	return -5;
			case EXPERT:
			case MASTER:	return -3;
			default:		return -7;
		}
	}
	
	private int getSizeHitBonus(Size size) {
		return size == Size.SMALL ? -1 : 1;
	}
	
	public Hit hitAgainstMonster(DamageSource damageSource, int damage, LivingEntity victim) {
		int roll = RandomUtils.jroll(20);
		int toHit = 1;
		
		if (getRightHand() != null && getRightHand().getStack().getItem() instanceof ItemWeapon) {
			ItemWeapon weapon = (ItemWeapon) getRightHand().getStack().getItem();
			
			toHit += weapon.getToHitBonus();
			
			// TODO: Add enchantment too
			
			toHit += getWeaponSkillHitBonus(getSkillLevel(weapon.getSkill()));
		}
		
		// TODO: rings of increase accuracy enchantment levels
		
		if (damageSource.getDamageType() == DamageSource.DamageType.MELEE) {
			toHit += 1;
			toHit += getStrengthHitBonus();
		}
		
		if (damageSource.getDamageType() == DamageSource.DamageType.MELEE ||
			damageSource.getDamageType() == DamageSource.DamageType.RANGED) {
			toHit += getDexterityHitBonus();
		}
		
		if (damageSource.getDamageType() == DamageSource.DamageType.RANGED) {
			toHit += getSizeHitBonus(victim.getSize());
		}
		
		toHit += getExperienceLevel();
		
		if (getExperienceLevel() < 3) {
			toHit += 1;
		}
		
		toHit += victim.getArmourClass();
		
		// TODO: ranged and spell
		
		getDungeon().entityAttacked(victim, victim.getX(), victim.getY(), roll, toHit);
		return toHit > roll ? new Hit(HitType.SUCCESS, damage) : new Hit(HitType.MISS, damage);
	}
	
	@Override
	public void swapHands() {
		super.swapHands();
		getDungeon().You("swap your weapons.");
	}
}
