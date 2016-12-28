package pw.lemmmy.jrogue.dungeon.items.comestibles;

import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;

public class ItemBanana extends ItemComestible {
	@Override
	public String getName(boolean requiresCapitalisation, boolean plural) {
		return (requiresCapitalisation ? "Banana" : "banana") + (plural ? "s" : "");
	}
	
	@Override
	public int getNutrition() {
		return 75;
	}
	
	@Override
	public float getWeight() {
		return 3;
	}
	
	@Override
	public int getTurnsRequiredToEat() {
		return 2;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_BANANA;
	}
}
