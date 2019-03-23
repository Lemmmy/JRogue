package jr.dungeon.serialisation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import jr.dungeon.Level;

import java.io.IOException;

public class LevelTypeAdapterFactory implements TypeAdapterFactory {
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		if (!Level.class.isAssignableFrom(type.getRawType())) return null;
		TypeAdapter<Level> levelTypeAdapter = gson.getDelegateAdapter(LevelTypeAdapterFactory.this, TypeToken.get(Level.class));
		
		return new TypeAdapter<T>() {
			@Override
			public void write(JsonWriter out, T rawValue) throws IOException {
				Level level = (Level) rawValue;
				
				JsonObject outLevel = new JsonObject();
				
				// serialise the exposed properties like normal (e.g. width, height)
				JsonObject serialisedLevel = levelTypeAdapter.toJsonTree(level).getAsJsonObject();
				serialisedLevel.entrySet().forEach(e -> outLevel.add(e.getKey(), e.getValue()));
				
				level.initialise();
				
				// serialise the stores
				level.tileStore.serialise(gson, outLevel);
				level.visibilityStore.serialise(gson, outLevel);
				level.lightStore.serialise(gson, outLevel);
				
				// TODO: what was this? it was called on every single level
				// dungeon.eventSystem.triggerEvent(new EntityAddedEvent(dungeon.getPlayer(), false));
				
				Streams.write(outLevel, out);
			}
			
			@Override
			public T read(JsonReader in) throws IOException {
				return null;
			}
		};
	}
}
