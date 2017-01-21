package pw.lemmmy.jrogue.dungeon.entities;

import org.json.JSONArray;
import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.containers.Container;
import pw.lemmmy.jrogue.dungeon.entities.containers.EntityItem;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.identity.Aspect;
import pw.lemmmy.jrogue.utils.RandomUtils;

import java.util.*;

public abstract class EntityLiving extends EntityTurnBased {
	private int health;
	protected int maxHealth;
	
	private int experienceLevel = 1;
	private int experience = 0;
	
	private int healingTurns = 0;
	
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
	
	public int getMaxHealth() {
		return maxHealth;
	}
	
	public int getHealth() {
		return health;
	}
	
	public void setHealth(int health) {
		this.health = health;
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
	
	public int getExperienceLevel() {
		return experienceLevel;
	}
	
	protected void setExperienceLevel(int level) {
		experienceLevel = level;
	}
	
	public int getExperience() {
		return experience;
	}
	
	public void setExperience(int experience) {
		this.experience = experience;
	}
	
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
				
				onLevelUp();
			}
		}
	}
	
	public void onLevelUp() {}
	
	public abstract int getMovementSpeed();
	
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
	
	public Map<Integer, Set<Class<? extends Aspect>>> getKnownAspects() {
		return knownAspects;
	}
	
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
	
	public Container.ContainerEntry getLeftHand() {
		return leftHand;
	}
	
	public void setLeftHand(Container.ContainerEntry leftHand) {
		this.leftHand = leftHand;
	}
	
	public Container.ContainerEntry getRightHand() {
		return rightHand;
	}
	
	public void setRightHand(Container.ContainerEntry rightHand) {
		this.rightHand = rightHand;
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
					e.printStackTrace();
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
	
	public boolean damage(DamageSource damageSource, int damage, EntityLiving attacker, boolean isPlayer) {
		int damageModifier = getDamageModifier(damageSource, damage);
		
		health = Math.max(0, health - damageModifier);
		healingTurns = 0;
		
		onDamage(damageSource, damage, attacker, isPlayer);
		
		if (health <= 0) {
			kill(damageSource, damage, attacker, isPlayer);
		}
		
		return health <= 0;
	}
	
	public int getDamageModifier(DamageSource damageSource, int damage) {
		return damage;
	}
	
	protected abstract void onDamage(DamageSource damageSource, int damage, EntityLiving attacker, boolean isPlayer);
	
	public void kill(DamageSource damageSource, int damage, EntityLiving attacker, boolean isPlayer) {
		health = 0;
		healingTurns = 0;
		
		onDie(damageSource, damage, attacker, isPlayer);
		
		getLevel().removeEntity(this);
	}
	
	protected abstract void onDie(DamageSource damageSource, int damage, EntityLiving attacker, boolean isPlayer);
	
	public void dropItem(ItemStack item) {
		if (leftHand != null && leftHand.getItem().equals(item.getItem())) {
			leftHand = null;
		}
		
		if (rightHand != null && rightHand.getItem().equals(item.getItem())) {
			rightHand = null;
		}
		
		List<Entity> entities = getLevel().getEntitiesAt(getX(), getY());
		
		Optional<Entity> ent = entities.stream()
			.filter(e -> e instanceof EntityItem && ((EntityItem) e).getItem() == item.getItem())
			.findFirst();
		
		if (ent.isPresent()) {
			EntityItem entItem = (EntityItem) ent.get();
			entItem.getItemStack().addCount(item.getCount());
		} else {
			EntityItem entityItem = new EntityItem(getDungeon(), getLevel(), getX(), getY(), item);
			getLevel().addEntity(entityItem);
		}
	}
	
	public enum Size {
		SMALL,
		LARGE
	}
}
