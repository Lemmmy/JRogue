package pw.lemmmy.jrogue.dungeon.items;

public class ItemGold extends Item {
	@Override
	public String getName(boolean requiresCapitalisation, boolean plural) {
		return requiresCapitalisation ? "Gold" : "gold";
	}

	@Override
	public int getWeight() {
		return 1;
	}

	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_GOLD;
	}

	public boolean isis() {
		return true;
	}
}
