package jr.dungeon.items.tools;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.*;
import jr.dungeon.items.Readable;
import jr.language.Lexicon;
import jr.language.Noun;

public class ItemKey extends Item {
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.skeletonKey.clone();
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
