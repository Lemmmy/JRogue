package jr.dungeon.items.identity;

import jr.utils.Serialisable;
import org.json.JSONObject;

public abstract class Aspect implements Serialisable {
	public abstract String getName();
	
	/**
	 * persistent aspects are known by all livingents of the same type permanently:
	 * 	 a bow's shape is persistent: once a player knows this style of bow is elven, it will know all future bows with
	 * 	 the same style are elven
	 *
	 * on the other hand, non-persistent aspects are per item instance:
	 *   an item's BUC status is non-persistent: it differs between all items
	 */
	public abstract boolean isPersistent();
	
	@Override
	public void serialise(JSONObject obj) {}
	
	@Override
	public void unserialise(JSONObject obj) {}
}
