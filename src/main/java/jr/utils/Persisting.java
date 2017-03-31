package jr.utils;

import org.json.JSONObject;

/**
 * Should be implemented by classes that have data persisting across game sessions.
 */
public interface Persisting {
	/**
	 * @return A JSONObject that will persist across game sessions. Its contents will appear in the dungeon save file.
	 */
    JSONObject getPersistence();

	default void serialisePersistence(JSONObject obj) {
        obj.put("persistence", getPersistence());
    }

    default void unserialisePersistence(JSONObject obj) {
        JSONObject p = obj.optJSONObject("persistence");

        if (p != null) {
            for (String key : p.keySet()) {
				getPersistence().put(key, p.get(key));
            }
        }
    }
}
