package pw.lemmmy.jrogue.dungeon.entities.containers;

import pw.lemmmy.jrogue.dungeon.items.ItemCategory;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;

public class WeaponRackContainer extends Container {
	public WeaponRackContainer(String name) {
		super(name);
	}
	
	@Override
	public boolean canAdd(ItemStack stack) {
		return super.canAdd(stack) && stack.getCategory().equals(ItemCategory.WEAPON);
	}
}
