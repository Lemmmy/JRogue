package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.utils.Utils;

public abstract class LivingEntity extends EntityTurnBased {
	public int health;
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

	public int getExperienceLevel() {
		return experienceLevel;
	}

	protected void setExperienceLevel(int level) {
		experienceLevel = level;
	}

	public boolean damage(DamageSource damageSource, int damage) {
		int damageModifier = getDamageModifier(damageSource, damage);

		health = Math.max(0, health - damageModifier);

		onDamage(damageSource, damage);

		if (health <= 0) {
			die(damageSource);
		}

		return health <= 0;
	}

	protected abstract void onDamage(DamageSource damageSource, int damage);

	protected void die(DamageSource damageSource) {
		onDie(damageSource);
	}

	protected abstract void onDie(DamageSource damageSource);

	public int getHealth() {
		return health;
	}

	public boolean isAlive() {
		return health > 0;
	}

	public abstract int getMovementSpeed();
}
