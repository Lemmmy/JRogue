package jr.utils;

import org.json.JSONObject;

public interface Serialisable {
	void serialise(JSONObject obj);
	
	void unserialise(JSONObject obj);
}
