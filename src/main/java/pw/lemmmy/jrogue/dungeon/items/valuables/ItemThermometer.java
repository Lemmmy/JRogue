package pw.lemmmy.jrogue.dungeon.items.valuables;

import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;
import pw.lemmmy.jrogue.dungeon.items.ItemCategory;
import pw.lemmmy.jrogue.dungeon.items.Shatterable;

public class ItemThermometer extends Item implements Shatterable {
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation, boolean plural) {
		String s = getBeatitudePrefix(observer, requiresCapitalisation);
		
		if (!s.isEmpty() && requiresCapitalisation) {
			requiresCapitalisation = false;
		}
		
		s += (requiresCapitalisation ? "Thermometer" : "thermometer") + (plural ? "s" : "");
		
		return s;
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
