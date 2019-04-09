package jr.dungeon.items.comestibles;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemCategory;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class ItemComestible extends Item {
    @Expose private int turnsEaten = 0;
    
    public EatenState getEatenState() {
        if (turnsEaten == 0) {
            return EatenState.UNEATEN;
        } else if (turnsEaten >= getTurnsRequiredToEat()) {
            return EatenState.EATEN;
        } else {
            return EatenState.PARTLY_EATEN;
        }
    }
    
    public int getTurnsRequiredToEat() {
        return 1;
    }
    
    public void eatPart() {
        turnsEaten++;
    }
    
    public List<StatusEffect> getStatusEffects(EntityLiving victim) {
        return null;
    }
    
    public abstract int getNutrition();
    
    @Override
    public ItemCategory getCategory() {
        return ItemCategory.COMESTIBLE;
    }
    
    @Override
    public boolean equals(Item other) {
        if (other instanceof ItemComestible) {
            ItemComestible otherComestible = (ItemComestible) other;
            
            if (getEatenState() != EatenState.UNEATEN || otherComestible.getEatenState() != EatenState.UNEATEN) {
                return false;
            }
        }
        
        return super.equals(other);
    }
    
    public enum EatenState {
        UNEATEN,
        PARTLY_EATEN,
        EATEN
    }
}
