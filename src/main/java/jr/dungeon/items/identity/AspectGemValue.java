package jr.dungeon.items.identity;

/**
 * Aspect of the value of a {@link jr.dungeon.items.valuables.ItemGem gem}, usually whether it's worthless or not.
 *
 * @see jr.dungeon.items.identity.Aspect
 */
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
