package pw.lemmmy.jrogue.dungeon.entities.monsters;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.AI;

import java.util.List;

public abstract class Monster extends LivingEntity {
	private AI ai;

	public Monster(Dungeon dungeon, Level level, int x, int y, int experienceLevel) {
		super(dungeon, level, x, y, experienceLevel);
	}

	public AI getAI() {
		return ai;
	}

	public void setAI(AI ai) {
		this.ai = ai;
	}

	@Override
	public void update() {
		super.update();

		if (ai != null) {
			ai.update();
		}
	}

	@Override
	protected void onWalk(LivingEntity walker, boolean isPlayer) {
	}

	@Override
	public boolean canBeWalkedOn() {
		return false;
	}

	public abstract int getWeight();

	public abstract int getNutrition();

	public abstract List<StatusEffect> getCorpseEffects(LivingEntity victim);

	public abstract int getVisibilityRange();

	public abstract boolean canMoveDiagonally();

	public abstract boolean canMeleeAttack();

	public abstract boolean canRangedAttack();

	;

	public abstract boolean canMagicAttack();

	;

	public void meleeAttackPlayer() {
	}

	;

	public void rangedAttackPlayer() {
	}

	public void magicAttackPlayer() {
	}
}
