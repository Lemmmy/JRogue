package pw.lemmmy.jrogue.dungeon.items.comestibles;

import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;

public class ItemApple extends ItemComestible {
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation, boolean plural) {
		String s = getBeatitudePrefix(observer, requiresCapitalisation);
		
		if (!s.isEmpty() && requiresCapitalisation) {
			requiresCapitalisation = false;
		}
		
		s += (requiresCapitalisation ? "Apple" : "apple") + (plural ? "s" : "");
		
		return s;
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
		return ItemAppearance.APPEARANCE_APPLE;
	}
}
