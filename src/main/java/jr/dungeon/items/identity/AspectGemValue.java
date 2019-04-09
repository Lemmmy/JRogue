package jr.dungeon.items.identity;

import jr.dungeon.serialisation.Registered;

/**
 * Aspect of the value of a {@link jr.dungeon.items.valuables.ItemGem gem}, usually whether it's worthless or not.
 *
 * @see jr.dungeon.items.identity.Aspect
 */
@Registered(id="aspectGemValue")
public class AspectGemValue extends Aspect {
    @Override
    public String getName() {
        return "Gem value";
    }
    
    @Override
    public boolean isPersistent() {
        return false;
    }
}
