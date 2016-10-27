package pw.lemmmy.jrogue.dungeon.entities.monsters;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.AI;

public abstract class Monster extends LivingEntity {
	private AI ai;

	public Monster(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}

	@Override
	public void update() {
		super.update();

		ai.update();
	}

	public abstract int getVisibilityRange();

	public abstract boolean canMeleeAttack();
	public abstract boolean canRangedAttack();
	public abstract boolean canMagicAttack();

	public abstract void meleeAttackPlayer();
	public abstract void rangedAttackPlayer();
	public abstract void magicAttackPlayer();
}
