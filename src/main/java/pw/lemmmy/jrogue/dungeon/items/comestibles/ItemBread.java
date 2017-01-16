package pw.lemmmy.jrogue.dungeon.items.comestibles;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;

public class ItemBread extends ItemComestible {
	@Override
	public String getName(LivingEntity observer, boolean requiresCapitalisation, boolean plural) {
		String s = getBeatitudePrefix(observer, requiresCapitalisation);
		
		if (!s.isEmpty() && requiresCapitalisation) {
			requiresCapitalisation = false;
		}
		
		s += (requiresCapitalisation ? "Loa" : "loa") + (plural ? "ves" : "f") + " of bread";
		
		return s;
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
