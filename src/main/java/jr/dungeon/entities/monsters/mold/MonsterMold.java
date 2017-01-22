package jr.dungeon.entities.monsters.mold;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.EntityLiving;
import jr.utils.RandomUtils;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.monsters.Monster;

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
	public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
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
	protected void onDamage(DamageSource damageSource, int damage, EntityLiving attacker, boolean isPlayer) {
		if (damageSource.getDamageType() == DamageSource.DamageType.MELEE) {
			int damageToDeal = RandomUtils.jroll(getExperienceLevel() + 1, 6);
			
			attacker.damage(DamageSource.MOLD_RETALIATION, damageToDeal, this, false);
		}
	}
}
