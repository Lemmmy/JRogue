package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;

public abstract class ItemWeapon extends Item implements Wieldable {
	public abstract void hit(LivingEntity attacker, LivingEntity victim);
	
	public abstract void zap(LivingEntity attacker, LivingEntity victim);
	
	public abstract void fire(LivingEntity attacker, LivingEntity victim);
	
	public abstract boolean isMelee();
	
	public abstract boolean isRanged();
	
	public abstract boolean isMagic();
	
	public abstract int getToHitBonus();
	
	public abstract Skill getSkill();
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.WEAPON;
	}
}
