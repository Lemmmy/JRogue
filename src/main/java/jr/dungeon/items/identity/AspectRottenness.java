package jr.dungeon.items.identity;

/**
 * Aspect of knowing whether a {@link jr.dungeon.items.comestibles.ItemComestible comestible} is rotten or not. This
 * is usually identified automatically when the player realises that something in their inventory 'really stinks'.
 */
public class AspectRottenness extends Aspect {
	@Override
	public String getName() {
		return "Rottenness";
	}
	
	@Override
	public boolean isPersistent() {
		return false;
	}
}
