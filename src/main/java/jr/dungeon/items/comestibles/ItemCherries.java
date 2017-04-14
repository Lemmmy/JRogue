package jr.dungeon.items.comestibles;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.ItemAppearance;

public class ItemCherries extends ItemComestible {
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation, boolean plural) {
		String s = getBeatitudePrefix(observer, requiresCapitalisation);
		
		if (!s.isEmpty() && requiresCapitalisation) {
			requiresCapitalisation = false;
		}
		
		s += plural ? requiresCapitalisation ? "Cherries" : "cherries" :
			 		  requiresCapitalisation ? "Cherry" : "cherry";
		
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
	public int getTurnsRequiredToEat() {
		return 2;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_CHERRIES;
	}
}
