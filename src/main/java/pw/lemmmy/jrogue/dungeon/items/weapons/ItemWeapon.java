package pw.lemmmy.jrogue.dungeon.items.weapons;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemCategory;
import pw.lemmmy.jrogue.dungeon.items.Wieldable;
import pw.lemmmy.jrogue.dungeon.items.projectiles.ItemProjectile;

public abstract class ItemWeapon extends Item implements Wieldable {
	public abstract void hit(LivingEntity attacker, LivingEntity victim);
	
	public abstract void zap(LivingEntity attacker, LivingEntity victim, int dx, int dy);
	
	public abstract boolean fire(LivingEntity attacker, ItemProjectile projectile, int dx, int dy);
	
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
