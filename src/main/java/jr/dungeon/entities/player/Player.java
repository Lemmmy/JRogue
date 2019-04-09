package jr.dungeon.entities.player;

import com.google.gson.annotations.Expose;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.*;
import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.effects.InjuredFoot;
import jr.dungeon.entities.effects.StrainedLeg;
import jr.dungeon.entities.events.EntityDeathEvent;
import jr.dungeon.entities.events.EntityEnergyChangedEvent;
import jr.dungeon.entities.events.EntityHealthChangedEvent;
import jr.dungeon.entities.events.EntityLevelledUpEvent;
import jr.dungeon.entities.monsters.ai.AStarPathfinder;
import jr.dungeon.entities.monsters.familiars.Familiar;
import jr.dungeon.entities.player.events.PlayerDefaultEvents;
import jr.dungeon.entities.player.roles.Role;
import jr.dungeon.entities.player.visitors.PlayerDefaultVisitors;
import jr.dungeon.entities.player.visitors.PlayerVisitor;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.entities.skills.SkillLevel;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventListener;
import jr.dungeon.events.EventPriority;
import jr.dungeon.items.Item;
import jr.dungeon.items.magical.spells.Spell;
import jr.dungeon.items.weapons.ItemWeapon;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Solidity;
import jr.dungeon.tiles.Tile;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Point;
import jr.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

@Registered(id="player")
public class Player extends EntityLiving {
	@Getter private AStarPathfinder pathfinder = new AStarPathfinder();
	
	@Expose private String name;
	@Expose @Getter private Role role;
	
	@Expose @Getter private int energy;
	@Expose @Getter private int maxEnergy;
	@Expose @Getter private int chargingTurns = 0;
	@Expose @Getter private Map<Character, Spell> knownSpells;
	
	@Expose @Getter @Setter private int nutrition;
	@Getter private NutritionState lastNutritionState;
	
	@Expose @Getter private Attributes attributes;
	@Expose @Getter private Map<Skill, SkillLevel> skills;
	
	@Expose @Getter private int gold = 0;
	
	@Expose @Getter @Setter private boolean godmode = false;
	
	// TODO: ability for multiple familiars, and for any entity to have familiars (not just players)
	@Getter private EntityReference<Familiar> familiar = new EntityReference<>();
	
	public PlayerDefaultVisitors defaultVisitors = new PlayerDefaultVisitors(this);
	private PlayerDefaultEvents defaultEvents = new PlayerDefaultEvents();
	
	public Player(Dungeon dungeon, Level level, Point position, String name, Role role) {
		super(dungeon, level, position, 1);
		
		this.name = name;
		this.role = role;
		
		nutrition = 1400;
		maxHealth = getMaxHealth();
		
		energy = maxEnergy = role.getMaxEnergy();
		knownSpells = new HashMap<>(role.getStartingSpells());
		
		if (JRogue.getSettings().getAttributes() != null) {
			attributes = JRogue.getSettings().getAttributes();
		} else {
			attributes = new Attributes();
			role.assignAttributes(attributes);
		}
		
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
		
		spawnFamiliar();
	}
	
	protected Player() { super(); }
	
	private void spawnFamiliar() {
		Class<? extends Familiar> familiarClass = getRole().getStartingFamiliar();
		if (familiarClass == null) return;
		
		Point spawnPoint;
		
		List<Tile> availableSpawnTiles = Arrays.stream(getLevel().tileStore.getOctAdjacentTiles(getPosition()))
			.filter(Objects::nonNull)
			.filter(t -> t.getType().getSolidity() == Solidity.WALK_ON)
			.collect(Collectors.toList());
		
		if (availableSpawnTiles.isEmpty()) {
			spawnPoint = getPosition();
		} else {
			spawnPoint = RandomUtils.randomFrom(availableSpawnTiles).position;
		}
		
		familiar.set(QuickSpawn.spawnClass(familiarClass, getLevel(), spawnPoint));
	}
	
	@Override
	public int getMaxHealth() {
		return 4 * (2 + getExperienceLevel()) + getConstitutionBonus();
	}
	
	public int getConstitutionBonus() {
		return attributes != null ? (int) Math.floor(0.25 * attributes.getAttribute(Attribute.CONSTITUTION) - 2) : 0;
	}
	
	@Override
	public int getHealingRate() {
		int constitution = attributes.getAttribute(Attribute.CONSTITUTION);
		
		switch (getNutritionState()) {
			case FAINTING:
				return Math.max(100 - constitution, 4);
			case STARVING:
				return Math.max(40 - constitution / 3, 4);
			default:
				return Math.max(20 - constitution / 2, 4);
		}
	}
	
	public int getChargingRate() {
		return (int) Math.max(Math.floor((38 - getExperienceLevel()) * (3.5f / 6f)), 4);
	}
	
	public void charge(int amount) {
		setEnergy(Math.min(maxEnergy, energy + amount));
	}
	
	private void levelUpEnergy() {
		int wisdom = attributes.getAttribute(Attribute.WISDOM);
		int gainMax = wisdom / 2 + 2;
		int gain = RandomUtils.roll(gainMax) + 2;
		
		maxEnergy += gain;
		charge(gain);
	}
	
	public void setEnergy(int energy) {
		int oldEnergy = this.energy;
		this.energy = energy;
		int newEnergy = this.energy;
		
		if (oldEnergy != newEnergy) {
			getDungeon().eventSystem
				.triggerEvent(new EntityEnergyChangedEvent(this, oldEnergy, newEnergy));
		}
	}
	
	public char getAvailableSpellLetter() {
		for (char letter : Container.INVENTORY_CHARS) {
			if (!knownSpells.containsKey(letter)) {
				return letter;
			}
		}
		
		return 0;
	}
	
	@Override
	public int getMovementSpeed() {
		int speed = super.getMovementSpeed();
		
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
	
	public NutritionState getNutritionState() {
		return NutritionState.fromNutrition(nutrition);
	}
	
	@Override
	public Noun getName(EntityLiving observer) {
		if (observer == this) {
			return Lexicon.you.clone();
		} else {
			return new Noun(name);
		}
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_PLAYER;
	}
	
	public void giveGold(int amount) {
		gold += amount;
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
		return getLevel().tileStore.getTile(getPosition()).getLightIntensity();
	}
	
	public int getCorridorVisibilityRange() {
		return 2 * ((getLightLevel() - 20) / 100) + 5;
	}
	
	public SkillLevel getSkillLevel(Skill skill) {
		return skills.getOrDefault(skill, SkillLevel.UNSKILLED);
	}
	
	public boolean isDebugger() {
		return name.equalsIgnoreCase("debugger");
	}
	
	@Override
	public void applyMovementPoints() {
		setMovementPoints(getMovementPoints() + getMovementSpeed());
	}
	
	@EventHandler(selfOnly = true)
	public void onLevelUp(EntityLevelledUpEvent event) {
		levelUpEnergy();
		
		getDungeon().greenYou("levelled up! You are now experience level %,d.", getExperienceLevel());
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
	
	@EventHandler(selfOnly = true)
	private void onHealthChanged(EntityHealthChangedEvent event) {
		if (event.getOldHealth() > event.getNewHealth()) {
			getDungeon().turnSystem.markSomethingHappened();
		}
	}
	
	private void updateEnergy() {
		setEnergy(Math.max(0, Math.min(maxEnergy, energy)));
		
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
			damage(new DamageSource(this, null, DamageType.CHOKING), 1);
		}
		
		nutrition--;
	}
	
	private void updateSpells() {
		knownSpells.values().forEach(Spell::update);
	}
	
	@EventHandler(selfOnly = true, priority = EventPriority.HIGHEST)
	private void onDie(EntityDeathEvent e) {
		DamageType type = e.getDamageSource().getType();
		
		if (type.getDeathString() != null) {
			getDungeon().log("[RED]" + type.getDeathString() + "[]");
		} else {
			getDungeon().redYou("die.");
		}
		
		getContainer().ifPresent(inv -> inv.getItems().forEach((character, itemStack) -> {
			Item i = itemStack.getItem();
			
			i.getAspects().forEach((aspectID, aspect) -> {
				if (aspect.isPersistent()) {
					observeAspect(i, aspectID);
				} else {
					i.observeAspect(this, aspectID);
				}
			});
		}));
		
		getDungeon().serialiser.deleteSave();
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return false;
	}
	
	public void acceptVisitor(PlayerVisitor visitor) {
		visitor.visit(this);
	}
	
	public boolean canCastSpell(Spell spell) {
		return energy >= spell.getCastingCost();
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
		
		if (damageSource.getType().getDamageClass() == DamageType.DamageClass.MELEE) {
			toHit += 1;
			toHit += getStrengthHitBonus();
		}
		
		if (damageSource.getType().getDamageClass() == DamageType.DamageClass.MELEE ||
			damageSource.getType().getDamageClass() == DamageType.DamageClass.RANGED) {
			toHit += getDexterityHitBonus();
		}
		
		if (damageSource.getType().getDamageClass() == DamageType.DamageClass.RANGED) {
			toHit += getSizeHitBonus(victim.getSize());
		}
		
		toHit += getExperienceLevel();
		
		if (getExperienceLevel() < 3) {
			toHit += 1;
		}
		
		toHit += victim.getArmourClass();
		
		// TODO: ranged and spell
		
		return toHit > roll ? new Hit(HitType.SUCCESS, damage) : new Hit(HitType.MISS, damage);
	}
	
	@Override
	public void swapHands() {
		super.swapHands();
		getDungeon().You("swap your weapons.");
	}
	
	@Override
	public Set<EventListener> getSubListeners() {
		val l = super.getSubListeners();
		l.add(attributes);
		l.add(defaultEvents);
		return l;
	}
}
