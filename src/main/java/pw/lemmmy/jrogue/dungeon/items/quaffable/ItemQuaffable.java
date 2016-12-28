package pw.lemmmy.jrogue.dungeon.items.quaffable;

import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemCategory;

public abstract class ItemQuaffable extends Item {
	public abstract void quaff(Entity entity);
	
	public abstract boolean canQuaff();
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.QUAFFABLE;
	}
}
