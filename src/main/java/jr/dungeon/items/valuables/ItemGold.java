package jr.dungeon.items.valuables;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.items.ItemCategory;
import jr.language.Lexicon;
import jr.language.Noun;

public class ItemGold extends Item {
	public boolean isNonCountable() {
		return true;
	}
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.gold.clone();
	}
	
	@Override
	public float getWeight() {
		return 0.01f;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_GOLD;
	}
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MISCELLANEOUS;
	}
}
