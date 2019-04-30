package jr.dungeon.items.comestibles;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;

@Wishable(name="lemon")
@Registered(id="itemLemon")
public class ItemLemon extends ItemComestible {
    @Override
    public Noun getBaseName(EntityLiving observer) {
        return Lexicon.lemon.clone();
    }
    
    @Override
    public int getNutrition() {
        return 60;
    }
    
    @Override
    public float getWeight() {
        return 2;
    }
    
    @Override
    public ItemAppearance getAppearance() {
        return ItemAppearance.APPEARANCE_LEMON;
    }
}
