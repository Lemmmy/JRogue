package pw.lemmmy.jrogue.dungeon.items.comestibles;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;

public class ItemCherries extends ItemComestible {
	@Override
	public String getName(LivingEntity observer, boolean requiresCapitalisation, boolean plural) {
		return plural ? (requiresCapitalisation ? "Pairs of cherries" : "pairs of cherries") :
			   (requiresCapitalisation ? "Pair of cherries" : "pair of cherries");
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
	public int getTurnsRequiredToEat() {
		return 2;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_CHERRIES;
	}
}
