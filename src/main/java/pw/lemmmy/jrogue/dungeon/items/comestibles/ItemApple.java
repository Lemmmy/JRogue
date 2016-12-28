package pw.lemmmy.jrogue.dungeon.items.comestibles;

import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;

public class ItemApple extends ItemComestible {
	@Override
	public String getName(boolean requiresCapitalisation, boolean plural) {
		return (requiresCapitalisation ? "Apple" : "apple") + (plural ? "s" : "");
	}
	
	@Override
	public int getNutrition() {
		return 50;
	}
	
	@Override
	public float getWeight() {
		return 2;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_APPLE;
	}
}
