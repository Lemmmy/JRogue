package pw.lemmmy.jrogue.dungeon.items.valuables;

import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;
import pw.lemmmy.jrogue.dungeon.items.ItemCategory;

public class ItemGold extends Item {
	public boolean isis() {
		return true;
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation, boolean plural) {
		return requiresCapitalisation ? "Gold" : "gold";
	}
	
	@Override
	public float getWeight() {
		return 0.01f;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_GOLD;
	}
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MISCELLANEOUS;
	}
}
