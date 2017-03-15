package jr.dungeon.items.identity;

/**
 * Aspect of knowledge of the subject of the book (e.g. a {@link jr.dungeon.items.magical.ItemSpellbook spellbook}).
 * Doesn't necessarily mean the player has fully read the book, just that they know what it's about.
 *
 * @see Aspect
 */
public class AspectBookContents extends Aspect {
	@Override
	public String getName() {
		return "Book contents";
	}
	
	@Override
	public boolean isPersistent() {
		return false;
	}
}
