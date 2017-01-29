package jr.dungeon.items.weapons;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemCategory;
import jr.dungeon.items.Wieldable;
import jr.dungeon.items.projectiles.ItemProjectile;

public abstract class ItemWeapon extends Item implements Wieldable {
	public abstract void hit(EntityLiving attacker, EntityLiving victim);
	
	public abstract void zap(EntityLiving attacker, EntityLiving victim, int dx, int dy);
	
	public abstract boolean fire(EntityLiving attacker, ItemProjectile projectile, int dx, int dy);
	
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
