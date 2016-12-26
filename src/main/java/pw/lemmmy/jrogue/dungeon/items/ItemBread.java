package pw.lemmmy.jrogue.dungeon.items;

public class ItemBread extends ItemComestible {
	@Override
	public String getName(boolean requiresCapitalisation, boolean plural) {
		return requiresCapitalisation ? "Bread" : "bread";
	}
	
	@Override
	public boolean isis() {
		return true;
	}
	
	@Override
	public int getNutrition() {
		return 500;
	}
	
	@Override
	public float getWeight() {
		return 20;
	}
	
	@Override
	public int getTurnsRequiredToEat() {
		return 2;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_BREAD;
	}
}
