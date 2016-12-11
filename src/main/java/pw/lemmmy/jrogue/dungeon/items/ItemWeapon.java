package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;

public abstract class ItemWeapon extends Item {
	public abstract void hit(LivingEntity attacker, LivingEntity victim);

	public abstract void zap(LivingEntity attacker, LivingEntity victim);

	public abstract void fire(LivingEntity attacker, LivingEntity victim);

	public abstract boolean isMelee();

	public abstract boolean isRanged();

	public abstract boolean isMagic();

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.WEAPON;
	}
}
