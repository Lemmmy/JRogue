package pw.lemmmy.jrogue.dungeon.items;

public class ItemCorn extends ItemComestible {
	@Override
	public String getName(boolean requiresCapitalisation, boolean plural) {
		return (requiresCapitalisation ? "Corn maize" : "corn maize") + (plural ? "s" : "");
	}

	@Override
	public int getNutrition() {
		return 250;
	}

	@Override
	public float getWeight() {
		return 10;
	}

	@Override
	public int getTurnsRequiredToEat() {
		return 2;
	}

	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_CORN;
	}
}
