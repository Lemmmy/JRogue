package pw.lemmmy.jrogue.dungeon.entities.monsters;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.AI;
import pw.lemmmy.jrogue.dungeon.items.ItemCorpse;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.List;

public abstract class Monster extends LivingEntity {
	private AI ai;

	public Monster(Dungeon dungeon, Level level, int x, int y) { // unserialisation constructor
		super(dungeon, level, x, y);

		this.setExperienceLevel(Math.abs(level.getDepth()));
	}

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
	public void kill(DamageSource damageSource, int damage, Entity attacker, boolean isPlayer) {
		if (attacker != null && attacker instanceof LivingEntity) {
			((LivingEntity) attacker).addExperience(getExperienceRewarded());
		}

		if (getCorpseChance() != 0f && Utils.randomFloat() <= getCorpseChance()) {
			drop(new ItemStack(new ItemCorpse(this)));
		}

		super.kill(damageSource, damage, attacker, isPlayer);
	}

	@Override
	public boolean canBeWalkedOn() {
		return false;
	}

	public abstract boolean isHostile();

	public abstract int getWeight();

	public abstract int getNutrition();

	public abstract float getCorpseChance();

	public abstract List<StatusEffect> getCorpseEffects(LivingEntity victim);

	public abstract int getVisibilityRange();

	public abstract boolean canMoveDiagonally();

	public abstract boolean canMeleeAttack();

	public abstract boolean canRangedAttack();

	public abstract boolean canMagicAttack();

	public void meleeAttackPlayer() {

	}

	public void rangedAttackPlayer() {

	}

	public void magicAttackPlayer() {

	}

	public int getExperienceRewarded() {
		return getSize() == Size.SMALL ? Utils.roll(1, 2) : Utils.roll(2, 2);
	}
}
