package jr.dungeon.entities;

import jr.ErrorHandler;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.effects.Paralysis;
import jr.dungeon.entities.events.EntityDamagedEvent;
import jr.dungeon.entities.events.EntityDeathEvent;
import jr.dungeon.entities.events.EntityLevelledUpEvent;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.identity.Aspect;
import jr.utils.RandomUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

@Getter
@Setter
public abstract class EntityLiving extends EntityTurnBased {
	private int health;
	protected int maxHealth;
	
	private int experienceLevel = 1;
	private int experience = 0;
	
	@Setter(AccessLevel.NONE)
	private int healingTurns = 0;
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Container inventory;
	
	private Container.ContainerEntry leftHand;
	private Container.ContainerEntry rightHand;
	
	/**
	 * known persistent aspects per item class
	 * the key is the hashcode of an items list of persistent aspects
	 */
	private final Map<Integer, Set<Class<? extends Aspect>>> knownAspects = new HashMap<>();
	
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
	
	public void heal(int amount) {
		health = Math.min(maxHealth, health + amount);
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
				
				getDungeon().triggerEvent(new EntityLevelledUpEvent(this, experienceLevel));
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
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("health", getHealth());
		obj.put("maxHealth", getMaxHealth());
		obj.put("experienceLevel", getExperienceLevel());
		obj.put("experience", getExperience());
		
		if (getContainer().isPresent()) {
			JSONObject serialisedInventory = new JSONObject();
			getContainer().get().serialise(serialisedInventory);
			
			obj.put("inventory", serialisedInventory);
			
			if (leftHand != null) {
				obj.put("leftHand", leftHand.getLetter());
			}
			
			if (rightHand != null) {
				obj.put("rightHand", rightHand.getLetter());
			}
		}
		
		JSONObject serialisedKnownAspects = new JSONObject();
		knownAspects.forEach((k, v) -> {
			JSONArray serialisedAspectList = new JSONArray();
			v.forEach(a -> serialisedAspectList.put(a.getName()));
			serialisedKnownAspects.put(k.toString(), serialisedAspectList);
		});
		obj.put("knownAspects", serialisedKnownAspects);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		health = obj.getInt("health");
		maxHealth = obj.getInt("maxHealth");
		experienceLevel = obj.getInt("experienceLevel");
		experience = obj.getInt("experience");
		
		if (obj.has("inventory")) {
			JSONObject serialisedInventory = obj.getJSONObject("inventory");
			setInventoryContainer(Container.createFromJSON(serialisedInventory));
			
			if (obj.has("leftHand")) {
				Character letter = obj.getString("leftHand").charAt(0);
				Optional<Container.ContainerEntry> entryOptional = inventory.get(letter);
				entryOptional.ifPresent(this::setLeftHand);
			}
			
			if (obj.has("rightHand")) {
				Character letter = obj.getString("rightHand").charAt(0);
				Optional<Container.ContainerEntry> entryOptional = inventory.get(letter);
				entryOptional.ifPresent(this::setRightHand);
			}
		}
		
		JSONObject serialisedKnownAspects = obj.getJSONObject("knownAspects");
		serialisedKnownAspects.keySet().forEach(k -> {
			Integer code = Integer.parseInt(k);
			JSONArray serialisedAspectList = serialisedKnownAspects.getJSONArray(k);
			
			Set<Class<? extends Aspect>> aspectSet = new HashSet<>();
			serialisedAspectList.forEach(c -> {
				try {
					aspectSet.add((Class<? extends Aspect>) Class.forName((String) c));
				} catch (ClassNotFoundException e) {
					ErrorHandler.error("Error unserialising EntityLiving", e);
				}
			});
			
			knownAspects.put(code, aspectSet);
		});
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
	
	public boolean damage(DamageSource damageSource, int damage, EntityLiving attacker) {
		int damageModifier = getDamageModifier(damageSource, damage);
		
		health = Math.max(0, health - damageModifier);
		healingTurns = 0;
		
		getDungeon().triggerEvent(new EntityDamagedEvent(this, attacker, damageSource, damage));
		
		if (health <= 0) {
			kill(damageSource, damage, attacker);
		}
		
		return health <= 0;
	}
	
	public int getDamageModifier(DamageSource damageSource, int damage) {
		return damage;
	}
	
	public void kill(DamageSource damageSource, int damage, EntityLiving attacker) {
		health = 0;
		healingTurns = 0;
		
		getDungeon().triggerEvent(new EntityDeathEvent(this, attacker, damageSource, damage));
		
		getLevel().getEntityStore().removeEntity(this);
	}
	
	public void dropItem(ItemStack item) {
		if (leftHand != null && leftHand.getItem().equals(item.getItem())) {
			leftHand = null;
		}
		
		if (rightHand != null && rightHand.getItem().equals(item.getItem())) {
			rightHand = null;
		}
		
		List<Entity> entities = getLevel().getEntityStore().getEntitiesAt(getX(), getY());
		
		Optional<Entity> ent = entities.stream()
			.filter(e -> e instanceof EntityItem && ((EntityItem) e).getItem() == item.getItem())
			.findFirst();
		
		if (ent.isPresent()) {
			EntityItem entItem = (EntityItem) ent.get();
			entItem.getItemStack().addCount(item.getCount());
		} else {
			EntityItem entityItem = new EntityItem(getDungeon(), getLevel(), getX(), getY(), item);
			getLevel().getEntityStore().addEntity(entityItem);
		}
	}
	
	public enum Size {
		SMALL,
		LARGE
	}
}
