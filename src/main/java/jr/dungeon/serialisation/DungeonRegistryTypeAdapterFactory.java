package jr.dungeon.serialisation;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class DungeonRegistryTypeAdapterFactory<T> implements TypeAdapterFactory {
	private Class<T> targetClass;
	
	public DungeonRegistryTypeAdapterFactory(Class<T> targetClass) {
		this.targetClass = targetClass;
	}
	
	public String getFieldName() {
		return "id";
	}
	
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		if (!targetClass.isAssignableFrom(type.getRawType())) return null;
		
		DungeonRegistry<? super T> registry = DungeonRegistries.findRegistryForClass(type.getRawType())
			.orElseThrow(() -> new RuntimeException(String.format("Can't create type adapter for `%s` because no registry exists", type.getRawType().getName())));
		
		return new TypeAdapter<T>() {
			@Override
			public void write(JsonWriter out, T value) throws IOException {
				Class<? extends T> valueClass = (Class<? extends T>) value.getClass();
				String id = registry.getID(valueClass)
					.orElseThrow(() -> new JsonParseException(String.format(
						"Attempting to serialise unregistered %s `%s`",
						targetClass.getSimpleName(), valueClass.getName()
					)));
				
				TypeAdapter<T> delegate = (TypeAdapter<T>) gson.getDelegateAdapter(DungeonRegistryTypeAdapterFactory.this, TypeToken.get(valueClass));
				
				JsonObject serialised = delegate.toJsonTree(value).getAsJsonObject();
				JsonObject clone = new JsonObject();
				
				if (serialised.has(getFieldName()))
					throw new JsonParseException(String.format("Cannot serialise %s `%s` because it already has a field named `%s`",
						targetClass.getSimpleName(), valueClass.getName(), getFieldName()
					));
				
				clone.add(getFieldName(), new JsonPrimitive(id));
				serialised.entrySet().forEach(e -> clone.add(e.getKey(), e.getValue()));
				
				Streams.write(clone, out);
			}
			
			@Override
			public T read(JsonReader in) {
				JsonElement element = Streams.parse(in);
				JsonElement sourceID = element.getAsJsonObject().remove(getFieldName()); // get and remove the id (dont bring it back to the object)
				
				if (sourceID == null)
					throw new JsonParseException(String.format(
						"Cannot unserialise %s because the `%s` field is missing",
						targetClass.getSimpleName(), getFieldName()
					));
				
				String id = sourceID.getAsString();
				Class clazz = registry.getClassFromID(id)
					.orElseThrow(() -> new JsonParseException(String.format(
						"Attempting to unserialise unregistered %s `%s`",
						targetClass.getSimpleName(), id
					)));
				
				TypeAdapter<T> delegate = (TypeAdapter<T>) gson.getDelegateAdapter(DungeonRegistryTypeAdapterFactory.this, TypeToken.get(clazz));
				T result = delegate.fromJsonTree(element);
				
				if (result instanceof Serialisable) {
					((Serialisable) result).afterDeserialise(); // TODO: may want to pass context here
				}
				
				return result;
			}
		};
	}
}
