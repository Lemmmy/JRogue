package jr.dungeon.items.tools;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.*;
import jr.dungeon.items.Readable;

public class ItemKey extends Item {
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation, boolean plural) {
		String s = getBeatitudePrefix(observer, requiresCapitalisation);
		
		if (!s.isEmpty() && requiresCapitalisation) {
			requiresCapitalisation = false;
		}
		
		s += (requiresCapitalisation ? "Skeleton key" : "skeleton key") + (plural ? "s" : "");
		
		return s;
	}
	
	@Override
	public float getWeight() {
		return 2;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_THERMOMETER;
	} // 4 now
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.TOOL;
	}
}
