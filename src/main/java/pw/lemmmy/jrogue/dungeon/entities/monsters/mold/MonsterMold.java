package pw.lemmmy.jrogue.dungeon.entities.monsters.mold;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;
import pw.lemmmy.jrogue.utils.RandomUtils;

import java.util.List;

public abstract class MonsterMold extends Monster {
	public MonsterMold(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	public MonsterMold(Dungeon dungeon, Level level, int x, int y, int experienceLevel) {
		super(dungeon, level, x, y, experienceLevel);
	}
	
	@Override
	public boolean isHostile() {
		return true;
	}
	
	@Override
	public int getWeight() {
		return 50;
	}
	
	@Override
	public int getNutrition() {
		return 30;
	}
	
	@Override
	public float getCorpseChance() {
		return 0;
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(LivingEntity victim) {
		return null;
	}
	
	@Override
	public int getVisibilityRange() {
		return 0;
	}
	
	@Override
	public boolean canMoveDiagonally() {
		return false;
	}
	
	@Override
	public boolean canMeleeAttack() {
		return false;
	}
	
	@Override
	public boolean canRangedAttack() {
		return false;
	}
	
	@Override
	public boolean canMagicAttack() {
		return false;
	}
	
	@Override
	public int getBaseArmourClass() {
		return 9;
	}
	
	@Override
	public int getMovementSpeed() {
		return 0;
	}
	
	@Override
	public Size getSize() {
		return Size.SMALL;
	}
	
	@Override
	protected void onDamage(DamageSource damageSource, int damage, LivingEntity attacker, boolean isPlayer) {
		if (damageSource.getDamageType() == DamageSource.DamageType.MELEE) {
			int damageToDeal = RandomUtils.jroll(getExperienceLevel() + 1, 6);
			
			attacker.damage(DamageSource.MOLD_RETALIATION, damageToDeal, this, false);
		}
	}
}
