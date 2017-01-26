package jr.dungeon.entities.player;

import jr.JRogue;
import jr.dungeon.entities.*;
import jr.dungeon.entities.player.visitors.*;
import jr.dungeon.items.comestibles.ItemComestible;
import jr.dungeon.tiles.Tile;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.effects.InjuredFoot;
import jr.dungeon.entities.effects.StrainedLeg;
import jr.dungeon.entities.monsters.ai.AStarPathfinder;
import jr.dungeon.entities.player.roles.Role;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.entities.skills.SkillLevel;
import jr.dungeon.items.magical.spells.Spell;
import jr.dungeon.items.weapons.ItemWeapon;
import jr.utils.RandomUtils;
import jr.utils.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Player extends EntityLiving {
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

	private final JSONObject persistence = new JSONObject();
	
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
		
		getContainer().ifPresent(container -> role.getStartingItems().forEach(i -> {
			Optional<Container.ContainerEntry> optionalEntry = container.add(i);
			
			optionalEntry.ifPresent(entry -> {
				if (role.getStartingLeftHand() == entry.getStack()) {
					setLeftHand(entry);
				}
				
				if (role.getStartingRightHand() == entry.getStack()) {
					setRightHand(entry);
				}
			});
		}));
		
		setHealth(getMaxHealth());
		setMovementPoints(Dungeon.NORMAL_SPEED);
	}
	
	@Override
	public int getMaxHealth() {
		return 5 * (2 + getExperienceLevel()) + getConstitutionBonus();
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
	
	public void setEnergy(int energy) {
		this.energy = energy;
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
	
	public char getAvailableSpellLetter() {
		for (char letter : Utils.INVENTORY_CHARS) {
			if (!knownSpells.containsKey(letter)) {
				return letter;
			}
		}
		
		return 0;
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
		return EntityLiving.Size.LARGE;
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
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
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
		return getLevel().getTileStore().getTile(getX(), getY()).getLightIntensity();
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
		
	public AStarPathfinder getPathfinder() {
		return pathfinder;
	}
	
	@Override
	public void applyMovementPoints() {
		setMovementPoints(getMovementPoints() + getMovementSpeed());
	}
	
	@Override
	public void onLevelUp() {
		levelUpEnergy();
		
		getDungeon().greenYou("levelled up! You are now experience level %,d.", getExperienceLevel());
		getDungeon().greenYou(
			"have %,d spendable skill point%s.",
			++spendableSkillPoints,
			spendableSkillPoints == 1 ? "" : "s"
		);
	}
	
	@Override
	public void update() {
		super.update();
		
		updateEnergy();
		updateNutrition();
		updateSpells();
		
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
				case CHOKING:
					getDungeon().redYou("are choking!");
					break;
				case HUNGRY:
					getDungeon().orangeYou("are starting to feel hungry.");
					break;
				case STARVING:
					getDungeon().redYou("are starving!");
					break;
				case FAINTING:
					getDungeon().redYou("are passing out due to starvation!");
					break;
				default:
					break;
			}
		}
		
		if (getNutritionState() == NutritionState.CHOKING) {
			damage(DamageSource.CHOKING, 1, this, true);
		}
		
		nutrition--;
	}
	
	private void updateSpells() {
		knownSpells.values().forEach(Spell::update);
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
	protected void onDamage(DamageSource damageSource, int damage, EntityLiving attacker, boolean isPlayer) {}
	
	@Override
	protected void onDie(DamageSource damageSource, int damage, EntityLiving attacker, boolean isPlayer) {
		if (damageSource.getDeathString() != null) {
			getDungeon().log("[RED]" + damageSource.getDeathString() + "[]");
		} else {
			getDungeon().redYou("die.");
		}
		
		getDungeon().deleteSave();
	}
	
	@Override
	protected void onKick(EntityLiving kicker, boolean isPlayer, int dx, int dy) {
		getDungeon().orangeYou("step on your own foot.");
	}
	
	@Override
	protected void onWalk(EntityLiving walker, boolean isPlayer) {}
	
	@Override
	public boolean canBeWalkedOn() {
		return false;
	}
	
	private void acceptVisitor(PlayerVisitor visitor) {
		visitor.visit(this);
	}
	
	public void teleport(int x, int y) {
		acceptVisitor(new PlayerTeleport(x, y));
	}
	
	public void walk(int dx, int dy) {
		acceptVisitor(new PlayerWalk(dx, dy));
	}
	
	public void travelDirectional() {
		acceptVisitor(new PlayerTravelDirectional());
	}
	
	public void travelPathfind(int tx, int ty) {
		acceptVisitor(new PlayerTravelPathfind(tx, ty));
	}
	
	public void kick() {
		acceptVisitor(new PlayerKick());
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
	
	public boolean canCastSpell(Spell spell) {
		return energy >= spell.getCastingCost();
	}
	
	private void castSpellNonDirectional(Spell spell) {
		acceptVisitor(new PlayerCastSpellNonDirectional(spell));
	}
	
	private void castSpellDirectional(Spell spell) {
		acceptVisitor(new PlayerCastSpellDirectional(spell));
	}
	
	public void eat() {
		acceptVisitor(new PlayerEat());
	}
	
	public void quaff() {
		acceptVisitor(new PlayerQuaff());
	}
	
	public void consume(ItemComestible item) {
		acceptVisitor(new PlayerConsume(item));
	}
	
	public void pickup() {
		acceptVisitor(new PlayerPickup());
	}
	
	public void drop() {
		acceptVisitor(new PlayerDrop());
	}
	
	public void loot() {
		acceptVisitor(new PlayerLoot());
	}
	
	public void wield() {
		acceptVisitor(new PlayerWield());
	}
	
	public void fire() {
		// TODO: quiver
	}
	
	public void throwItem() {
		acceptVisitor(new PlayerThrowItem());
	}
	
	public void climbAny() {
		acceptVisitor(new PlayerClimbAny());
	}
	
	public void climbUp() {
		acceptVisitor(new PlayerClimbUp());
	}
	
	public void climbDown() {
		acceptVisitor(new PlayerClimbDown());
	}
	
	public void climb(Tile tile, boolean up) {
		acceptVisitor(new PlayerClimb(tile, up));
	}
	
	public void read() {
		acceptVisitor(new PlayerRead());
	}
	
	public Hit hitFromMonster(DamageSource damageSource, int damage, EntityLiving attacker) {
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
		return (int) Math.floor(strength / 5);
	}
	
	private int getDexterityHitBonus() {
		int dexterity = getAttributes().getAttribute(Attribute.DEXTERITY);
		return dexterity < 15 ? (int) Math.floor(dexterity / 5) - 2 : dexterity - 15;
	}
	
	private int getWeaponSkillHitBonus(SkillLevel skillLevel) {
		switch (skillLevel) {
			case UNSKILLED:
				return -4;
			case ADVANCED:
				return 2;
			case EXPERT:
			case MASTER:
				return 3;
			default:
				return 0;
		}
	}
	
	private int getSizeHitBonus(Size size) {
		return size == Size.SMALL ? -1 : 1;
	}
	
	public Hit hitAgainstMonster(DamageSource damageSource, int damage, EntityLiving victim) {
		int roll = RandomUtils.jroll(20);
		int toHit = 1;
		
		if (getRightHand() != null && getRightHand().getItem() instanceof ItemWeapon) {
			ItemWeapon weapon = (ItemWeapon) getRightHand().getItem();
			
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

	@Override
	public JSONObject getPersistence() {
		return persistence;
	}
}
