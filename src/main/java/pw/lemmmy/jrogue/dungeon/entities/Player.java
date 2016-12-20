package pw.lemmmy.jrogue.dungeon.entities;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.Prompt;
import pw.lemmmy.jrogue.dungeon.entities.actions.*;
import pw.lemmmy.jrogue.dungeon.entities.effects.InjuredFoot;
import pw.lemmmy.jrogue.dungeon.entities.effects.StrainedLeg;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.AStarPathFinder;
import pw.lemmmy.jrogue.dungeon.entities.roles.Role;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.entities.skills.SkillLevel;
import pw.lemmmy.jrogue.dungeon.items.*;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Player extends LivingEntity {
	private String name;
	private Role role;

	private int nutrition;
	private NutritionState lastNutritionState;

	private int strength;
	private int agility;
	private int dexterity;
	private int constitution;
	private int intelligence;
	private int wisdom;
	private int charisma;

	private int gold = 0;

	private boolean godmode = false;
	private Map<Skill, SkillLevel> skills;

	public Player(Dungeon dungeon, Level level, int x, int y) { // unserialisation constructor
		super(dungeon, level, x, y);
	}

	public Player(Dungeon dungeon, Level level, int x, int y, String name, Role role) {
		super(dungeon, level, x, y, 1);

		this.name = name;
		this.role = role;

		nutrition = 1000;

		strength = role.getStrength() +
			(int) ((float) Math.ceil(role.getStrength() * Utils.randomFloat(role.getStrengthRemaining())));
		agility = role.getAgility() +
			(int) ((float) Math.ceil(role.getAgility() * Utils.randomFloat(role.getAgilityRemaining())));
		dexterity = role.getDexterity() +
			(int) ((float) Math.ceil(role.getDexterity() * Utils.randomFloat(role.getDexterityRemaining())));
		constitution = role.getConstitution() +
			(int) ((float) Math.ceil(role.getConstitution() * Utils.randomFloat(role.getConstitutionRemaining())));
		intelligence = role.getIntelligence() +
			(int) ((float) Math.ceil(role.getIntelligence() * Utils.randomFloat(role.getIntelligenceRemaining())));
		wisdom = role.getWisdom() +
			(int) ((float) Math.ceil(role.getWisdom() * Utils.randomFloat(role.getWisdomRemaining())));
		charisma = role.getCharisma() +
			(int) ((float) Math.ceil(role.getCharisma() * Utils.randomFloat(role.getCharismaRemaining())));

		setInventoryContainer(new Container("Inventory"));
		skills = new HashMap<>(role.getStartingSkills());

		if (getContainer().isPresent()) {
			role.getStartingItems().forEach(i -> {
				Optional<Container.ContainerEntry> entry = getContainer().get().add(i);

				if (entry.isPresent() && role.getStartingLeftHand() == entry.get().getStack()) {
					setLeftHand(entry.get());
				}

				if (entry.isPresent() && role.getStartingRightHand() == entry.get().getStack()) {
					setRightHand(entry.get());
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
		return (int) Math.floor(0.25 * getConstitution() - 2);
	}

	public int getConstitution() {
		return constitution;
	}

	@Override
	public int getHealingRate() {
		if (getNutritionState() == NutritionState.FAINTING) {
			return 100 - constitution;
		} else if (getNutritionState() == NutritionState.STARVING) {
			return 40 - (constitution / 3);
		} else {
			return 20 - (constitution / 2);
		}
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
		if (godmode) {
			return 0;
		}

		return super.getDamageModifier(damageSource, damage);
	}

	@Override
	public int getDepth() {
		return 3;
	}

	@Override
	public Size getSize() {
		return LivingEntity.Size.LARGE;
	}

	public Role getRole() {
		return role;
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

	public int getStrength() {
		return strength;
	}

	public int getAgility() {
		return agility;
	}

	public int getDexterity() {
		return dexterity;
	}

	public int getIntelligence() {
		return intelligence;
	}

	public int getWisdom() {
		return wisdom;
	}

	public int getCharisma() {
		return charisma;
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
		if (!skills.containsKey(skill)) {
			return SkillLevel.UNSKILLED;
		} else {
			return skills.get(skill);
		}
	}

	public boolean isDebugger() {
		return name.equalsIgnoreCase("debugger");
	}

	public void godmode() {
		this.godmode = true;
	}

	@Override
	public void calculateMovement() {
		setMovementPoints(getMovementPoints() + getMovementSpeed());
	}

	@Override
	public void update() {
		super.update();

		if (getHealth() > getMaxHealth()) {
			setHealth(getMaxHealth());
		}

		if (godmode) {
			setHealth(getMaxHealth());
		}

		if (getNutritionState() != lastNutritionState) {
			lastNutritionState = getNutritionState();

			switch (getNutritionState()) {
				case CHOKING:
					getDungeon().redYou("are choking!");
					break;
				case FAINTING:
				case STARVING:
					getDungeon().redYou("are starving!");
					break;
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
		obj.put("strength", getStrength());
		obj.put("agility", getAgility());
		obj.put("dexterity", getDexterity());
		obj.put("constitution", getConstitution());
		obj.put("intelligence", getIntelligence());
		obj.put("wisdom", getWisdom());
		obj.put("charisma", getCharisma());
		obj.put("nutrition", getNutrition());
		obj.put("gold", getGold());
		obj.put("godmode", godmode);

		JSONObject serialisedSkills = new JSONObject();
		skills.entrySet().forEach(e -> serialisedSkills.put(e.getKey().name(), e.getValue().name()));
		obj.put("skills", serialisedSkills);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void unserialise(JSONObject obj) {
		setInventoryContainer(new Container("Inventory"));
		skills = new HashMap<>();

		super.unserialise(obj);

		name = obj.getString("name");
		strength = obj.getInt("strength");
		agility = obj.getInt("agility");
		dexterity = obj.getInt("dexterity");
		constitution = obj.getInt("constitution");
		intelligence = obj.getInt("intelligence");
		wisdom = obj.getInt("wisdom");
		charisma = obj.getInt("charisma");
		nutrition = obj.getInt("nutrition");
		gold = obj.getInt("gold");
		godmode = obj.getBoolean("godmode");

		String roleClassName = obj.getString("role");
		try {
			Class roleClass = Class.forName(roleClassName);
			Constructor roleConstructor = roleClass.getConstructor();
			role = (Role) roleConstructor.newInstance();
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
	}

	@Override
	protected void onDamage(DamageSource damageSource, int damage, Entity attacker, boolean isPlayer) {

	}

	@Override
	protected void onDie(DamageSource damageSource) {
		if (damageSource.getDeathString() != null) {
			getDungeon().log("[RED]" + damageSource.getDeathString() + "[]");
		} else {
			getDungeon().redYou("die");
		}
	}

	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {
		getDungeon().You("step on your own foot.");
	}

	@Override
	protected void onWalk(LivingEntity walker, boolean isPlayer) {
	}

	@Override
	public boolean canBeWalkedOn() {
		return false;
	}

	public void teleport(int x, int y) {
		setAction(new ActionTeleport(getDungeon(), this, x, y));
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
				} else {
					walkAction(tile, newX, newY);
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
			setAction(new ActionMove(getDungeon(), this, x, y));
		} else if (tile.getType() == TileType.TILE_ROOM_DOOR_LOCKED) {
			getDungeon().The("door is locked.");
		} else if (tile.getType() == TileType.TILE_ROOM_DOOR_CLOSED) {
			tile.setType(TileType.TILE_ROOM_DOOR_OPEN);
			getDungeon().You("open the door.");
		}
	}

	public void travelDirectional() {
		getDungeon().prompt(new Prompt("Travel in what direction?", null, true, new Prompt.PromptCallback() {
			@Override
			public void onNoResponse() {
				getDungeon().log("Nevermind.");
			}

			@Override
			public void onInvalidResponse(char response) {
			}

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
					setAction(new ActionMove(getDungeon(), Player.this, getX() + dx, getY() + dy));
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

		Path path = AStarPathFinder.findPath(
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

			if (stop.get()) return;
			if (getX() == step.getX() && getY() == step.getY()) return;

			if (step.getType().getSolidity() == TileType.Solidity.SOLID) {
				stop.set(true);
				return;
			}

			int oldX = getX();
			int oldY = getY();

			pathTaken.addStep(step);
			setAction(new ActionMove(getDungeon(), Player.this, step.getX(), step.getY()));
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
		getDungeon().prompt(new Prompt("Kick in what direction?", null, true, new Prompt.PromptCallback() {
			@Override
			public void onNoResponse() {
				getDungeon().log("Nevermind.");
			}

			@Override
			public void onInvalidResponse(char response) {
			}

			@Override
			public void onResponse(char response) {
				if (!Utils.MOVEMENT_CHARS.containsKey(response)) {
					getDungeon().log(String.format("Invalid direction '[YELLOW]%s[]'.", response));
					return;
				}

				if (wisdom > 5 && hasStatusEffect(InjuredFoot.class)) {
					getDungeon().Your("foot is in no shape for kicking.");
					return;
				}

				if (wisdom > 5 && hasStatusEffect(StrainedLeg.class)) {
					getDungeon().Your("leg is in no shape for kicking.");
					return;
				}

				Integer[] d = Utils.MOVEMENT_CHARS.get(response);
				int dx = d[0];
				int dy = d[1];

				if (getLevel().getEntitiesAt(getX() + dx, getY() + dy).size() > 0) {
					setAction(new ActionKick(
						getDungeon(),
						Player.this,
						d,
						getLevel().getEntitiesAt(getX() + dx, getY() + dy).get(0)
					));
				} else {
					setAction(new ActionKick(getDungeon(), Player.this, d));
				}

				getDungeon().turn();
			}
		}));
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

		String promptString;

		if (item.isis()) {
			promptString = String.format(
				"There is [YELLOW]%s[] here. Eat it?",
				item.getName(false, false)
			);
		} else {
			if (item.beginsWithVowel()) {
				promptString = String.format(
					"There is an [YELLOW]%s[] here. Eat it?",
					item.getName(false, false)
				);
			} else {
				promptString = String.format(
					"There is a [YELLOW]%s[] here. Eat it?",
					item.getName(false, false)
				);
			}
		}

		getDungeon().prompt(new Prompt(promptString, new char[]{'y', 'n'}, true, new Prompt.PromptCallback() {
			@Override
			public void onNoResponse() {
				getDungeon().log("Nevermind.");
			}

			@Override
			public void onInvalidResponse(char response) {
				getDungeon().log(String.format("Invalid response '[YELLOW]%s[]'.", response));
			}

			@Override
			public void onResponse(char response) {
				if (response == 'n') {
					eatFromInventory();
					return;
				}

				setAction(new ActionEat(
					getDungeon(),
					Player.this,
					item,
					new EntityAction.ActionCallback() {
						@Override
						public void onComplete() {
							super.onComplete();

							if (item.getEatenState() == ItemComestible.EatenState.EATEN) {
								if (stack.getCount() == 1) {
									entity.getLevel().removeEntity(entity);
								} else {
									stack.subtractCount(1);
								}
							}
						}
					}
				));

				getDungeon().turn();
			}
		}));
	}

	private void eatFromInventory() {
		if (!getContainer().isPresent()) {
			getDungeon().You("have nothing to eat.");
			return;
		}

		Container inventory = getContainer().get();

		Map<Character, ItemStack> comestibles = getComestiblesInInventory();

		if (comestibles.size() == 0) {
			getDungeon().You("have nothing to eat.");
			return;
		}

		char[] options = ArrayUtils.toPrimitive(comestibles.keySet().toArray(new Character[0]));
		options = Arrays.copyOf(options, options.length + 1);
		options[options.length - 1] = '-';

		getDungeon().prompt(new Prompt("Eat what?", options, true, new Prompt.PromptCallback() {
			@Override
			public void onNoResponse() {
				getDungeon().log("Nevermind.");
			}

			@Override
			public void onInvalidResponse(char response) {
				getDungeon().log(String.format("Invalid item '[YELLOW]%s[]'.", response));
			}

			@Override
			public void onResponse(char letter) {
				Optional<Container.ContainerEntry> containerEntry = inventory.get(letter);

				if (!containerEntry.isPresent()) {
					getDungeon().log(String.format("Invalid item '[YELLOW]%s[]'.", letter));
					return;
				}

				ItemStack stack = containerEntry.get().getStack();
				ItemComestible item = (ItemComestible) stack.getItem();

				setAction(new ActionEat(
					getDungeon(),
					Player.this,
					item,
					new EntityAction.ActionCallback() {
						@Override
						public void onComplete() {
							super.onComplete();

							if (item.getEatenState() == ItemComestible.EatenState.EATEN) {
								if (stack.getCount() == 1) {
									inventory.remove(containerEntry.get().getLetter());
								} else {
									stack.subtractCount(1);
								}
							}
						}
					}
				));

				getDungeon().turn();
			}
		}));
	}

	public void consume(ItemComestible item) {
		if (item.getTurnsRequiredToEat() == 1) {
			getDungeon().You("eat the %s.", item.getName(false, false));
			nutrition += item.getNutrition();

			item.eatPart();
			return;
		}

		if (item.getEatenState() != ItemComestible.EatenState.EATEN) {
			if (item.getTurnsEaten() == item.getTurnsRequiredToEat() - 1) {
				getDungeon().You("finish eating the %s.", item.getName(false, false));

				nutrition += Math.ceil(item.getNutrition() / item.getTurnsRequiredToEat());

				if (item.getStatusEffects(this) != null) {
					item.getStatusEffects(this).forEach(this::addStatusEffect);
				}
			} else {
				getDungeon().You("eat a part of the %s.", item.getName(false, false));

				nutrition += Math.floor(item.getNutrition() / item.getTurnsRequiredToEat());

				if (item.getStatusEffects(this) != null &&
					getNutritionState() != NutritionState.STARVING && getNutritionState() != NutritionState.FAINTING &&
					getWisdom() > 6) {

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
						if (stack.beginsWithVowel()) {
							getDungeon().You(
								"pick up an [YELLOW]%s[] ([YELLOW]%s[]).",
								stack.getName(false),
								result.get().getLetter()
							);
						} else {
							getDungeon().You(
								"pick up a [YELLOW]%s[] ([YELLOW]%s[]).",
								stack.getName(false),
								result.get().getLetter()
							);
						}
					}

					break;
				} else {
					getDungeon().You("can't hold anything!");
				}
			}
		}
	}

	public void drop() {
		if (!getContainer().isPresent()) {
			getDungeon().You("can't hold anything!");
			return;
		}

		Container inventory = getContainer().get();

		char[] options = ArrayUtils.toPrimitive(inventory.getItems().keySet().toArray(new Character[0]));

		getDungeon().prompt(new Prompt("Drop what?", options, true, new Prompt.PromptCallback() {
			@Override
			public void onNoResponse() {
				getDungeon().log("Nevermind.");
			}

			@Override
			public void onInvalidResponse(char response) {
				getDungeon().log(String.format("Invalid item '[YELLOW]%s[]'.", response));
			}

			@Override
			public void onResponse(char letter) {
				Optional<Container.ContainerEntry> containerEntry = inventory.get(letter);

				if (!containerEntry.isPresent()) {
					getDungeon().log(String.format("Invalid item '[YELLOW]%s[]'.", letter));
					return;
				}

				ItemStack stack = containerEntry.get().getStack();
				Item item = stack.getItem();

				inventory.remove(letter);

				EntityItem entityItem = new EntityItem(getDungeon(), getLevel(), stack, getX(), getY());
				getLevel().addEntity(entityItem);

				if (item.isis() || stack.getCount() > 1) {
					getDungeon().You("drop [YELLOW]%s[] ([YELLOW]%s[]).", stack.getName(false), letter);
				} else {
					if (stack.beginsWithVowel()) {
						getDungeon().You("drop an [YELLOW]%s[] ([YELLOW]%s[]).", stack.getName(false), letter);
					} else {
						getDungeon().You("drop a [YELLOW]%s[] ([YELLOW]%s[]).", stack.getName(false), letter);
					}
				}

				getDungeon().turn();
			}
		}));
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
		if (!getContainer().isPresent()) {
			getDungeon().You("can't wield anything!");
			return;
		}

		Container inventory = getContainer().get();

		Map<Character, ItemStack> wieldables = getWieldablesInInventory();

		if (wieldables.size() == 0) {
			getDungeon().You("have nothing to wield.");
			return;
		}

		char[] options = ArrayUtils.toPrimitive(wieldables.keySet().toArray(new Character[0]));
		options = Arrays.copyOf(options, options.length + 1);
		options[options.length - 1] = '-';

		getDungeon().prompt(new Prompt("Wield what?", options, true, new Prompt.PromptCallback() {
			@Override
			public void onNoResponse() {
				getDungeon().log("Nevermind.");
			}

			@Override
			public void onInvalidResponse(char response) {
				getDungeon().log(String.format("Invalid item '[YELLOW]%s[]'.", response));
			}

			@Override
			public void onResponse(char letter) {
				if (letter == '-') {
					setLeftHand(null);
					setRightHand(null);
					getDungeon().You("unwield everything.");
					return;
				}

				Optional<Container.ContainerEntry> containerEntry = inventory.get(letter);

				if (!containerEntry.isPresent()) {
					getDungeon().log(String.format("Invalid item '[YELLOW]%s[]'.", letter));
					return;
				}

				ItemStack stack = containerEntry.get().getStack();
				Item item = stack.getItem();

				if (getRightHand() != null && ((Wieldable) getRightHand().getStack().getItem()).isTwoHanded()) {
					setLeftHand(null);
				}

				setRightHand(containerEntry.get());

				if (((Wieldable) item).isTwoHanded()) {
					setLeftHand(containerEntry.get());
				}

				if (item.isis() || stack.getCount() > 1) {
					getDungeon().You("wield [YELLOW]%s[] ([YELLOW]%s[]).", stack.getName(false), letter);
				} else {
					if (stack.beginsWithVowel()) {
						getDungeon().You("wield an [YELLOW]%s[] ([YELLOW]%s[]).", stack.getName(false), letter);
					} else {
						getDungeon().You("wield a [YELLOW]%s[] ([YELLOW]%s[]).", stack.getName(false), letter);
					}
				}
			}
		}));
	}

	private Map<Character, ItemStack> getWieldablesInInventory() {
		if (getContainer().isPresent()) {
			return getContainer().get().getItems().entrySet().stream()
								 .filter(e -> e.getValue().getItem() instanceof Wieldable)
								 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		} else {
			return Collections.emptyMap();
		}
	}

	private Map<Character, ItemStack> getComestiblesInInventory() {
		if (getContainer().isPresent()) {
			return getContainer().get().getItems().entrySet().stream()
								 .filter(e -> e.getValue().getItem() instanceof ItemComestible)
								 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		} else {
			return Collections.emptyMap();
		}
	}

	public void swapHands() {
		Container.ContainerEntry left = getLeftHand();
		Container.ContainerEntry right = getRightHand();

		setLeftHand(right);
		setRightHand(left);

		getDungeon().You("swap your weapons.");
	}

	public enum NutritionState {
		CHOKING("Choking", 2),
		STUFFED("Stuffed", 1),
		NOT_HUNGRY("Not hungry"),
		HUNGRY("Hungry", 1),
		STARVING("Starving", 2),
		FAINTING("Fainting", 2);

		private String string;
		private int importance = 0;

		NutritionState(String string) {
			this(string, 0);
		}

		NutritionState(String string, int importance) {
			this.string = string;
			this.importance = importance;
		}

		@Override
		public String toString() {
			return string;
		}

		public int getImportance() {
			return importance;
		}
	}
}
