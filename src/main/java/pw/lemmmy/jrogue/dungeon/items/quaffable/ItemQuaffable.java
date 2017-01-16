package pw.lemmmy.jrogue.dungeon.items.quaffable;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemCategory;

public abstract class ItemQuaffable extends Item {
	public abstract void quaff(LivingEntity quaffer);
	
	public abstract boolean canQuaff(LivingEntity quaffer);
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.QUAFFABLE;
	}
}
