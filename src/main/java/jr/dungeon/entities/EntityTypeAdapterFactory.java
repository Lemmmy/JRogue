package jr.dungeon.entities;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class EntityTypeAdapterFactory implements TypeAdapterFactory {
	private static final String ENTITY_FIELD_NAME = "id";
	
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		// TODO: consider throwing an exception here, I may have gotten these the wrong way round
		if (!Entity.class.isAssignableFrom(type.getRawType())) return null;
		
		return new TypeAdapter<T>() {
			@Override
			public void write(JsonWriter out, T value) throws IOException {
				Class<? extends Entity> entityClass = (Class<? extends Entity>) value.getClass();
				String id = EntityRegistry.getEntityID(entityClass)
					.orElseThrow(() -> new JsonParseException(String.format("Attempting to serialise unregistered entity `%s`", entityClass.getName())));
				
				TypeAdapter<T> delegate = (TypeAdapter<T>) gson.getDelegateAdapter(EntityTypeAdapterFactory.this, TypeToken.get(entityClass));
				
				JsonObject serialised = delegate.toJsonTree(value).getAsJsonObject();
				JsonObject clone = new JsonObject();
				
				if (serialised.has(ENTITY_FIELD_NAME))
					throw new JsonParseException(String.format("Cannot serialise entity `%s` because it already has a field named `%s`", entityClass.getName(), ENTITY_FIELD_NAME));
				
				clone.add(ENTITY_FIELD_NAME, new JsonPrimitive(id));
				serialised.entrySet().forEach(e -> clone.add(e.getKey(), e.getValue()));
				
				Streams.write(clone, out);
			}
			
			@Override
			public T read(JsonReader in) {
				JsonElement element = Streams.parse(in);
				JsonElement sourceID = element.getAsJsonObject().remove(ENTITY_FIELD_NAME); // get and remove the id (dont bring it back to the object)
				
				if (sourceID == null)
					throw new JsonParseException(String.format("Cannot unserialise entity because the `%s` field is missing", ENTITY_FIELD_NAME));
				
				String id = sourceID.getAsString();
				Class<? extends Entity> entityClass = EntityRegistry.getEntityClass(id)
					.orElseThrow(() -> new JsonParseException(String.format("Attempting to unserialise unregistered entity `%s`", id)));
				
				TypeAdapter<T> delegate = (TypeAdapter<T>) gson.getDelegateAdapter(EntityTypeAdapterFactory.this, TypeToken.get(entityClass));
				return delegate.fromJsonTree(element);
				
				// TODO: Gson does not call constructors when parsing.
				//       If we have any behaviour that depends on unserialisation constructors,
				//       we will have to make some alternative method to be called instead,
				//       e.g. Entity.init()
			}
		};
	}
}
