package pw.lemmmy.jrogue.dungeon.items.comestibles;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;

public class ItemCorn extends ItemComestible {
	@Override
	public String getName(LivingEntity observer, boolean requiresCapitalisation, boolean plural) {
		return (requiresCapitalisation ? "Ear" : "ear") + (plural ? "s" : "") + " of corn";
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
