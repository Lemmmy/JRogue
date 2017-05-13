package jr.dungeon.items.identity;

import jr.dungeon.language.Noun;
import jr.utils.Serialisable;
import org.json.JSONObject;

/**
 * An aspect is something an entity knows about an item. A persistent aspect is known by all entities with the same
 * type forever, a non-persistent is specific to that item instance and will differ between all items.
 *
 * An example of a persistent aspect is the shape of a bow. When a player identifies the shape, they will permanently
 * remember the name of the shape, and associate it with all other bows with the same shape.
 *
 * An example of a non-persistent aspect is the beatitude (blessed, uncursed or cursed status) of an item. The
 * beatitude differs between every item, so it's non-persistent.
 */
public abstract class Aspect implements Serialisable {
	public abstract String getName();
	
	public Noun applyNameTransformers(Noun name) {
		return name;
	}
	
	/**
	 * persistent aspects are known by all livingents of the same type permanently:
	 * 	 a bow's shape is persistent: once a player knows this style of bow is elven, it will know all future bows with
	 * 	 the same style are elven
	 *
	 * on the other hand, non-persistent aspects are per item instance:
	 *   an item's BUC status is non-persistent: it differs between all items
	 *
	 * @return Whether or not the Aspect is persistent.
	 */
	public abstract boolean isPersistent();
	
	@Override
	public void serialise(JSONObject obj) {}
	
	@Override
	public void unserialise(JSONObject obj) {}
}
