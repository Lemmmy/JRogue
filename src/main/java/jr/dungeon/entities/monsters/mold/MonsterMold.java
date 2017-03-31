package jr.dungeon.entities.monsters.mold;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSourceType;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityDamagedEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.events.DungeonEventHandler;
import jr.utils.RandomUtils;

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
	
	@DungeonEventHandler(selfOnly = true)
	public void onDamage(EntityDamagedEvent e) {
		if (
			e.getDamageSource().getDamageType() == DamageSourceType.DamageType.MELEE &&
			RandomUtils.jroll(1, 4) == 1
		) {
			e.getAttacker().damage(DamageSourceType.MOLD_RETALIATION, 1, this);
		}
	}
}
