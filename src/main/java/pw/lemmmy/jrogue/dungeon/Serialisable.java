package pw.lemmmy.jrogue.dungeon;

import org.json.JSONObject;

public interface Serialisable {
    void serialise(JSONObject obj);

    void unserialise(JSONObject obj);
}
