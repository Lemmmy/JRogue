package pw.lemmmy.jrogue.dungeon.items.valuables;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;
import pw.lemmmy.jrogue.dungeon.items.ItemCategory;
import pw.lemmmy.jrogue.dungeon.items.Shatterable;

public class ItemThermometer extends Item implements Shatterable {
	@Override
	public String getName(LivingEntity observer, boolean requiresCapitalisation, boolean plural) {
		return (requiresCapitalisation ? "Thermometer" : "thermometer") + (plural ? "s" : "");
	}
	
	@Override
	public float getWeight() {
		return 2;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_THERMOMETER;
	}
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MISCELLANEOUS;
	}
}
