package jr.utils;

import org.json.JSONObject;

public interface Serialisable {
	/**
	 * Serialises the object into the given JSONObject.
	 * @param obj The JSONObject to serialise into.
	 */
	void serialise(JSONObject obj);

	/**
	 * Unserialises the object from the given JSONObject by mutating the object.
	 * @param obj The JSONObject to unserialise from.
	 */
	void unserialise(JSONObject obj);
}
