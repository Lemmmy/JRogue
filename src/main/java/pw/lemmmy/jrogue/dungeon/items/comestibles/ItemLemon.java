package pw.lemmmy.jrogue.dungeon.items.comestibles;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;

public class ItemLemon extends ItemComestible {
	@Override
	public String getName(LivingEntity observer, boolean requiresCapitalisation, boolean plural) {
		return (requiresCapitalisation ? "Lemon" : "lemon") + (plural ? "s" : "");
	}
	
	@Override
	public int getNutrition() {
		return 60;
	}
	
	@Override
	public float getWeight() {
		return 2;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_LEMON;
	}
}
