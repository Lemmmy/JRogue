package jr.dungeon;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public interface LevelStore {
	void initialise();
	
	void serialise(Gson gson, JsonObject out);
	void deserialise(Gson gson, JsonObject in);
}
