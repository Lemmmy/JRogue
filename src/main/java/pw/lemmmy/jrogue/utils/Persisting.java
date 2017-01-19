package pw.lemmmy.jrogue.utils;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Serialisable;

public interface Persisting extends Serialisable {
    JSONObject getPersistence();

    @Override
    default void serialise(JSONObject obj) {
        obj.put("persistence", getPersistence());
    }

    @Override
    default void unserialise(JSONObject obj) {
        for (String key : obj.getJSONObject("persistence").keySet()) {
            getPersistence().put(key, obj.get(key));
        }
    }
}
