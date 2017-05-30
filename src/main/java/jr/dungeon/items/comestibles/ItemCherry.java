package jr.dungeon.items.comestibles;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.ItemAppearance;
import jr.language.Lexicon;
import jr.language.Noun;

public class ItemCherry extends ItemComestible {
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.cherry.clone();
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
		return ItemAppearance.APPEARANCE_CHERRY;
	}
}
