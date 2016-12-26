package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.entities.Entity;

public abstract class ItemDrinkable extends Item {
	public abstract void drink(Entity entity);
	
	public abstract boolean canDrink();
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.DRINKABLE;
	}
}
