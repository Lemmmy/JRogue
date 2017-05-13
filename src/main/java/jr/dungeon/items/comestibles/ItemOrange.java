package jr.dungeon.items.comestibles;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.language.Lexicon;
import jr.dungeon.language.Noun;

public class ItemOrange extends ItemComestible {
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.orange.clone();
	}
	
	@Override
	public int getNutrition() {
		return 75;
	}
	
	@Override
	public float getWeight() {
		return 2;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_ORANGE;
	}
}
