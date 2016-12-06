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
	public int getWeight() {
		return 1;
	}

	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_GOLD;
	}
}
