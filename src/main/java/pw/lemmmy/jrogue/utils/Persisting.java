package pw.lemmmy.jrogue.utils;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Serialisable;

public interface Persisting {
    JSONObject getPersistence();

    default void serialisePersistence(JSONObject obj) {
        obj.put("persistence", getPersistence());
    }

    default void unserialisePersistence(JSONObject obj) {
        for (String key : obj.getJSONObject("persistence").keySet()) {
            getPersistence().put(key, obj.get(key));
        }
    }
}
