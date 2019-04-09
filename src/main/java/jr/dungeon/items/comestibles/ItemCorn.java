package jr.dungeon.items.comestibles;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;

@Wishable(name="corn")
@Registered(id="itemCorn")
public class ItemCorn extends ItemComestible {
    @Override
    public Noun getName(EntityLiving observer) {
        return Lexicon.corn.clone();
    }
    
    @Override
    public int getNutrition() {
        return 250;
    }
    
    @Override
    public float getWeight() {
        return 10;
    }
    
    @Override
    public int getTurnsRequiredToEat() {
        return 2;
    }
    
    @Override
    public ItemAppearance getAppearance() {
        return ItemAppearance.APPEARANCE_CORN;
    }
}
