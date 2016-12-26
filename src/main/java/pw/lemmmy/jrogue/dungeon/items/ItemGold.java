package pw.lemmmy.jrogue.dungeon.items;

public class ItemGold extends Item {
	public boolean isis() {
		return true;
	}
	
	@Override
	public String getName(boolean requiresCapitalisation, boolean plural) {
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
