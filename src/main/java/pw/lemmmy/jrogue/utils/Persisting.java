package pw.lemmmy.jrogue.utils;

import org.json.JSONObject;

public interface Persisting {
    JSONObject getPersistence();

    default void serialisePersistence(JSONObject obj) {
        obj.put("persistence", getPersistence());
    }

    default void unserialisePersistence(JSONObject obj) {
        JSONObject p = obj.optJSONObject("persistence");

        if (p != null) {
            for (String key : p.keySet()) {
                getPersistence().put(key, obj.get(key));
            }
        }
    }
}
