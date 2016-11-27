package pw.lemmmy.jrogue.dungeon.entities;

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

	public int getDamageModifier(DamageSource damageSource, int damage) {
		return damage;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getExperienceLevel() {
		return experienceLevel;
	}

	protected void setExperienceLevel(int level) {
		experienceLevel = level;
	}

	public boolean damage(DamageSource damageSource, int damage, Entity attacker, boolean isPlayer) {
		int damageModifier = getDamageModifier(damageSource, damage);

		health = Math.max(0, health - damageModifier);

		onDamage(damageSource, damage, attacker, isPlayer);

		if (health <= 0) {
			die(damageSource);
		}

		return health <= 0;
	}

	protected abstract void onDamage(DamageSource damageSource, int damage, Entity attacker, boolean isPlayer);

	protected void die(DamageSource damageSource) {
		onDie(damageSource);

		getLevel().removeEntity(this);
	}

	protected abstract void onDie(DamageSource damageSource);

	public int getHealth() {
		return health;
	}

	public boolean isAlive() {
		return health > 0;
	}

	public abstract int getMovementSpeed();

	public abstract Size getSize();

	public void drop(ItemStack item) {
		List<Entity> entities = getLevel().getEntitiesAt(getX(), getY());

		Optional<Entity> ent = entities.stream()
			.filter(e -> e instanceof EntityItem && ((EntityItem) e).getItem() == item.getItem())
			.findFirst();

		if (ent.isPresent()) {
			EntityItem entItem = (EntityItem)ent.get();
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
