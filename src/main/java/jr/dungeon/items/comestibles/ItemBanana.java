package jr.dungeon.items.comestibles;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;

@Wishable(name="banana")
@Registered(id="itemBanana")
public class ItemBanana extends ItemComestible {
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.banana.clone();
	}
	
	@Override
	public int getNutrition() {
		return 75;
	}
	
	@Override
	public float getWeight() {
		return 3;
	}
	
	@Override
	public int getTurnsRequiredToEat() {
		return 2;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_BANANA;
	}
}
