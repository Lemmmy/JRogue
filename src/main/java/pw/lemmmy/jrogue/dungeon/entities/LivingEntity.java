package pw.lemmmy.jrogue.dungeon.entities;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.List;
import java.util.Optional;

public abstract class LivingEntity extends EntityTurnBased {
	private int health;
	private int maxHealth;

	private int experienceLevel = 1;

	private Container inventory;

	private Container.ContainerEntry leftHand;
	private Container.ContainerEntry rightHand;

	public LivingEntity(Dungeon dungeon, Level level, int x, int y, int experienceLevel) {
		super(dungeon, level, x, y);

		maxHealth = getBaseMaxHealth();
		health = getMaxHealth();
	}

	private int getBaseMaxHealth() {
		return experienceLevel = Utils.roll(experienceLevel, 6);
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public int getExperienceLevel() {
		return experienceLevel;
	}

	protected void setExperienceLevel(int level) {
		experienceLevel = level;
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

	public abstract int getMovementSpeed();

	@Override
	public int getDepth() {
		switch (getSize()) {
			case SMALL:
				return 1;

			case LARGE:
				return 2;

			default:
				return 0;
		}
	}

	public abstract Size getSize();

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

	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);

		obj.put("health", getHealth());
		obj.put("maxHealth", getMaxHealth());
		obj.put("experienceLevel", getExperienceLevel());

		if (getContainer().isPresent()) {
			JSONObject serialisedInventory = new JSONObject();
			getContainer().get().serialise(serialisedInventory);

			obj.put("inventory", serialisedInventory);

			obj.put("leftHand", leftHand.getLetter());
			obj.put("rightHand", rightHand.getLetter());
		}
	}

	public boolean damage(DamageSource damageSource, int damage, Entity attacker, boolean isPlayer) {
		int damageModifier = getDamageModifier(damageSource, damage);

		health = Math.max(0, health - damageModifier);

		onDamage(damageSource, damage, attacker, isPlayer);

		if (health <= 0) {
			kill(damageSource);
		}

		return health <= 0;
	}

	public int getDamageModifier(DamageSource damageSource, int damage) {
		return damage;
	}

	protected abstract void onDamage(DamageSource damageSource, int damage, Entity attacker, boolean isPlayer);

	public void kill(DamageSource damageSource) {
		onDie(damageSource);

		getLevel().removeEntity(this);
	}

	protected abstract void onDie(DamageSource damageSource);

	public void drop(ItemStack item) {
		List<Entity> entities = getLevel().getEntitiesAt(getX(), getY());

		Optional<Entity> ent = entities.stream()
									   .filter(e -> e instanceof EntityItem && ((EntityItem) e).getItem() == item
										   .getItem())
									   .findFirst();

		if (ent.isPresent()) {
			EntityItem entItem = (EntityItem) ent.get();
			entItem.getItemStack().addCount(item.getCount());
		} else {
			EntityItem entityItem = new EntityItem(getDungeon(), getLevel(), item, getX(), getY());
			getLevel().addEntity(entityItem);
		}
	}

	public enum Size {
		SMALL,
		LARGE
	}
}
