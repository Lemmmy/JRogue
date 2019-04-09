package jr.dungeon.items.comestibles;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;

@Wishable(name="bread")
@Registered(id="itemBread")
public class ItemBread extends ItemComestible {
    @Override
    public Noun getName(EntityLiving observer) {
        return Lexicon.bread.clone();
    }
    
    @Override
    public int getNutrition() {
        return 500;
    }
    
    @Override
    public float getWeight() {
        return 20;
    }
    
    @Override
    public int getTurnsRequiredToEat() {
        return 2;
    }
    
    @Override
    public ItemAppearance getAppearance() {
        return ItemAppearance.APPEARANCE_BREAD;
    }
}
