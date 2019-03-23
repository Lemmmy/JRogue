package jr.dungeon.entities;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.effects.Paralysis;
import jr.dungeon.entities.events.EntityDamagedEvent;
import jr.dungeon.entities.events.EntityDeathEvent;
import jr.dungeon.entities.events.EntityHealthChangedEvent;
import jr.dungeon.entities.events.EntityLevelledUpEvent;
import jr.dungeon.entities.interfaces.ContainerOwner;
import jr.dungeon.events.EventListener;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.identity.Aspect;
import jr.utils.RandomUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

@Getter
@Setter
public abstract class EntityLiving extends EntityTurnBased implements ContainerOwner {
	/**
	 * The Entity's health.
	 */
	@Expose private int health;
	/**
	 * The Entity's maximum health.
	 */
	@Expose protected int maxHealth;
	
	/**
	 * The Entity's experience level - i.e. how much they've levelled up.
	 */
	@Expose private int experienceLevel = 1;
	/**
	 * The Entity's progress through their current experience level.
	 *
	 * @see #getXPForLevel(int)
	 */
	@Expose private int experience = 0;
	
	/**
	 * The current turn counter for the Entity's healing cooldown.
	 */
	@Setter(AccessLevel.NONE)
	@Expose private int healingTurns = 0;
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Expose private Container inventory;
	
	private Container.ContainerEntry leftHand;
	private Container.ContainerEntry rightHand;
	
	@Expose private Character leftHandLetter;
	@Expose private Character rightHandLetter;
	
	/**
	 * known persistent aspects per item class
	 * the key is the hashcode of an items list of persistent aspects
	 */
	@Expose private final Map<Integer, Set<Class<? extends Aspect>>> knownAspects = new HashMap<>();
	
	public EntityLiving(Dungeon dungeon, Level level, int x, int y) { // unserialisation constructor
		this(dungeon, level, x, y, 1);
	}
	
	public EntityLiving(Dungeon dungeon, Level level, int x, int y, int experienceLevel) {
		super(dungeon, level, x, y);
		
		maxHealth = getBaseMaxHealth();
		health = getMaxHealth();
	}
	
	protected int getBaseMaxHealth() {
		return RandomUtils.roll(experienceLevel, 6);
	}
	
	public boolean isAlive() {
		return health > 0;
	}
	
	public void setHealth(int health) {
		int oldHealth = this.health;
		this.health = health;
		int newHealth = this.health;
		
		if (oldHealth != newHealth) {
			getDungeon().eventSystem
				.triggerEvent(new EntityHealthChangedEvent(this, oldHealth, newHealth));
		}
	}
	
	public void heal(int amount) {
		setHealth(Math.min(maxHealth, health + amount));
	}
	
	public int getHealingRate() {
		return 40;
	}
	
	public int getArmourClass() {
		return getBaseArmourClass();
	}
	
	public abstract int getBaseArmourClass();
	
	public int getXPForLevel(int level) {
		return (int) Math.pow((float) level / 1.75f, 2) * 2 + 10;
	}
	
	public void addExperience(int experience) {
		int xpForLevel = getXPForLevel(experienceLevel);
		
		for (int i = 0; i < experience; i++) {
			if (++this.experience > xpForLevel) {
				experienceLevel++;
				this.experience = 0;
				
				xpForLevel = getXPForLevel(experienceLevel);
				
				getDungeon().eventSystem.triggerEvent(new EntityLevelledUpEvent(this, experienceLevel));
			}
		}
	}
	
	public int getMovementSpeed() {
		if (!hasStatusEffect(Paralysis.class)) {
			return Dungeon.NORMAL_SPEED;
		} else {
			return 0;
		}
	}
	
	@Override
	public int getDepth() {
		switch (getSize()) {
			case SMALL:
				return 3;
			
			case LARGE:
				return 4;
			
			default:
				return 0;
		}
	}
	
	public abstract Size getSize();
	
	public boolean isAspectKnown(Item item, Class<? extends Aspect> aspectClass) {
		return knownAspects.get(item.getPersistentAspects().hashCode()).contains(aspectClass);
	}
	
	public void observeAspect(Item item, Class<? extends Aspect> aspectClass) {
		int code = item.getPersistentAspects().hashCode();
		
		if (!knownAspects.containsKey(code)) {
			knownAspects.put(code, new HashSet<>());
		}
		
		knownAspects.get(code).add(aspectClass);
	}
	
	@Override
	public Optional<Container> getContainer() {
		return Optional.ofNullable(inventory);
	}
	
	protected void setInventoryContainer(Container container) {
		this.inventory = container;
	}
	
	public void swapHands() {
		Container.ContainerEntry left = getLeftHand();
		Container.ContainerEntry right = getRightHand();
		
		setLeftHand(right);
		setRightHand(left);
	}
	
	@Override
	public void beforeSerialise() {
		if (leftHand != null) leftHandLetter = leftHand.getLetter();
		if (rightHand != null) rightHandLetter = rightHand.getLetter();
	}
	
	@Override
	public void afterDeserialise() {
		if (leftHandLetter != null) leftHand = inventory.get(leftHandLetter).orElse(null);
		if (rightHandLetter != null) rightHand = inventory.get(rightHandLetter).orElse(null);
	}
	
	@Override
	public Set<EventListener> getSubListeners() {
		val subListeners = super.getSubListeners();
		
		getContainer().ifPresent(c -> {
			subListeners.add(c);
			subListeners.addAll(c.getSubListeners());
		});
		
		return subListeners;
	}
	
	@Override
	public void update() {
		super.update();
		
		setHealth(Math.max(0, Math.min(getMaxHealth(), getHealth())));
		
		if (getHealth() < getMaxHealth()) {
			healingTurns++;
		}
		
		if (healingTurns >= getHealingRate()) {
			heal(1);
			healingTurns = 0;
		}
		
		if (inventory != null) {
			inventory.update(this);
		}
	}
	
	public boolean damage(DamageSource damageSource, int damage) {
		int damageModifier = getDamageModifier(damageSource, damage);
		
		setHealth(Math.max(0, health - damageModifier));
		healingTurns = 0;
		
		getDungeon().eventSystem.triggerEvent(new EntityDamagedEvent(this, damageSource, damage));
		
		if (health <= 0) {
			kill(damageSource, damage);
		}
		
		return health <= 0;
	}
	
	public int getDamageModifier(DamageSource damageSource, int damage) {
		return damage;
	}
	
	public void kill(DamageSource damageSource, int damage) {
		health = 0;
		healingTurns = 0;
		
		getDungeon().eventSystem.triggerEvent(new EntityDeathEvent(this, damageSource, damage));
		remove();
	}
	
	public void dropItem(ItemStack item) {
		if (leftHand != null && leftHand.getItem().equals(item.getItem())) {
			leftHand = null;
		}
		
		if (rightHand != null && rightHand.getItem().equals(item.getItem())) {
			rightHand = null;
		}
		
		List<Entity> entities = getLevel().entityStore.getEntitiesAt(getX(), getY());
		
		Optional<Entity> ent = entities.stream()
			.filter(e -> e instanceof EntityItem && ((EntityItem) e).getItem() == item.getItem())
			.findFirst();
		
		if (ent.isPresent()) {
			EntityItem entItem = (EntityItem) ent.get();
			entItem.getItemStack().addCount(item.getCount());
		} else {
			EntityItem entityItem = new EntityItem(getDungeon(), getLevel(), getX(), getY(), item);
			getLevel().entityStore.addEntity(entityItem);
		}
	}
	
	@Override
	public ToStringBuilder toStringBuilder() {
		ToStringBuilder tsb = super.toStringBuilder()
			.append("health", String.format("%,d/%,d", health, maxHealth))
			.append("healingTurns", String.format("%,d", healingTurns))
			.append("xp", String.format("lvl %,d, %,d xp", experienceLevel, experience))
			.append("rightHand", rightHand != null ? rightHand.getItem().toStringBuilder() : "none")
			.append("leftHand", leftHand != null ? leftHand.getItem().toStringBuilder() : "none")
			.append("hasInventory", getContainer().isPresent());
		
		knownAspects.forEach((h, a) -> tsb.append(h.toString(), StringUtils.join(a.stream().map(Class::getSimpleName).toArray(String[]::new))));
		
		return tsb;
	}
	
	public enum Size {
		SMALL,
		LARGE
	}
}
