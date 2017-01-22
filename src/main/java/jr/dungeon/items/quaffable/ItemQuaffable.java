package jr.dungeon.items.quaffable;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemCategory;

public abstract class ItemQuaffable extends Item {
	public abstract void quaff(EntityLiving quaffer);
	
	public abstract boolean canQuaff(EntityLiving quaffer);
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.QUAFFABLE;
	}
}
