package jr.dungeon.items.comestibles;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;

@Wishable(name="carrot")
@Registered(id="itemCarrot")
public class ItemCarrot extends ItemComestible {
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.carrot.clone();
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
		return ItemAppearance.APPEARANCE_CARROT;
	}
}
