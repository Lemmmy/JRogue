package jr.dungeon.entities.containers;

import jr.dungeon.items.ItemCategory;
import jr.dungeon.items.ItemStack;
import jr.language.Noun;

public class WeaponRackContainer extends Container {
    public WeaponRackContainer(Noun name) {
        super(name);
    }
    
    public WeaponRackContainer(String name) {
        super(name);
    }
    
    @Override
    public boolean canAdd(ItemStack stack) {
        return super.canAdd(stack) && stack.getCategory().equals(ItemCategory.WEAPON);
    }
}
