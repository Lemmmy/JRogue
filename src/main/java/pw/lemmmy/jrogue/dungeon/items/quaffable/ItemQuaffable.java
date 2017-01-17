package pw.lemmmy.jrogue.dungeon.items.quaffable;

import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemCategory;

public abstract class ItemQuaffable extends Item {
	public abstract void quaff(EntityLiving quaffer);
	
	public abstract boolean canQuaff(EntityLiving quaffer);
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.QUAFFABLE;
	}
}
