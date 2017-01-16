package pw.lemmmy.jrogue.dungeon.items.comestibles;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;

public class ItemCarrot extends ItemComestible {
	@Override
	public String getName(LivingEntity observer, boolean requiresCapitalisation, boolean plural) {
		return (requiresCapitalisation ? "Carrot" : "carrot") + (plural ? "s" : "");
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
		return ItemAppearance.APPEARANCE_CARROT;
	}
}
