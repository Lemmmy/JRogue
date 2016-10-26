package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;

public abstract class LivingEntity extends Entity {
	public int health;

	public LivingEntity(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}

	public abstract int getMaxHealth();

	public int getDamageModifier(DamageSource damageSource, int damage) {
		return damage;
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
