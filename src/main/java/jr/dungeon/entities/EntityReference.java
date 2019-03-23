package jr.dungeon.entities;

import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;

import java.io.IOException;
import java.util.UUID;

/**
 * Stores a reference to an {@link Entity} within a level via its {@link UUID}. This is primarily used
 * for serialisation. The entity will be serialised as just a {@link JsonPrimitive JSON string} with
 * the UUID:
 *
 * <pre>
 * {
 *     "myEntity": "06f46bcc-6325-4376-9fbd-3e687b21e250"
 * }
 * </pre>
 *
 * When evaluated using {@link #get}, it will search the given {@link Dungeon} or {@link Level} for
 * the entity, and cache it for later.
 */
@JsonAdapter(EntityReference.EntityReferenceTypeAdapter.class)
public class EntityReference<T extends Entity> {
	private UUID uuid;
	private T entity;
	
	/**
	 * Stores an already evaluated {@link Entity} reference.
	 *
	 * @param entity The {@link Entity} to store.
	 */
	public EntityReference(T entity) {
		this.entity = entity;
		this.uuid = entity.getUUID();
	}
	
	/**
	 * Stores an unevaluated {@link Entity} reference.
	 *
	 * @param uuid The {@link UUID} of the entity to look up later.
	 */
	public EntityReference(UUID uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * Stores an empty reference.
	 */
	public EntityReference() {}
	
	/**
	 * Return the value of the {@link Entity} reference. If the reference has not yet been evaluated,
	 * it will search every {@link Level} in the {@link Dungeon} for the entity. If it is not found,
	 * it will return <code>null</code>.
	 *
	 * @param dungeon The {@link Dungeon} to search for the {@link Entity} in.
	 * @return The {@link Entity} if one was found, or else <code>null</code>.
	 */
	public T get(Dungeon dungeon) {
		if (uuid == null) return null;
		if (entity != null) return entity;
		
		for (Level level : dungeon.getLevels().values()) {
			entity = (T) level.entityStore.getEntityByUUID(uuid);
			if (entity != null) return entity;
		}
		
		return null;
	}
	
	/**
	 * Return the value of the {@link Entity} reference. If the reference has not yet been evaluated,
	 * it will search the given {@link Level} for it. If it is not found, it will return
	 * <code>null</code>.
	 *
	 * @param level The {@link Level} to search for the {@link Entity} in.
	 * @return The {@link Entity} if one was found, or else <code>null</code>.
	 */
	public T get(Level level) {
		if (uuid == null) return null;
		if (entity != null) return entity;
		return entity = (T) level.entityStore.getEntityByUUID(uuid);
	}
	
	/**
	 * Sets the value of the {@link Entity} reference to an already evaluated entity.
	 *
	 * @param entity The {@link Entity} to set the reference to.
	 * @return The same {@link Entity} (for chaining).
	 */
	public T set(T entity) {
		this.entity = entity;
		this.uuid = entity.getUUID();
		return entity;
	}
	
	/**
	 * Sets the value of the {@link Entity} reference to an unevaluated entity {@link UUID}.
	 * @param uuid The {@link UUID} to set the reference to.
	 */
	public void set(UUID uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * @return Whether or not a UUID for this entity is set. This does not necessarily mean
	 * 	       that the {@link Entity} definitely exists.
	 */
	public boolean isSet() {
		return this.uuid != null;
	}
	
	/**
	 * Unsets this entity reference, making the {@link UUID} and {@link Entity} null.
	 */
	public void unset() {
		this.uuid = null;
		this.entity = null;
	}
	
	/**
	 * Return the value of the {@link Entity} reference if it is set and can be found. If the
	 * reference has not yet been evaluated, it will search every {@link Level} in the
	 * {@link Dungeon} for the entity. If it is not found, it will return, it will return
	 * <code>other</code>.
	 *
	 * @param dungeon The {@link Dungeon} to search for the {@link Entity} in.
	 * @param other the value to be returned if there is no value present, may be
	 *              <code>null</code>.
	 * @return The {@link Entity} if one was found, or else <code>other</code>.
	 */
	public T orElse(Dungeon dungeon, T other) {
		if (uuid == null) return other;
		T got = get(dungeon);
		return got != null ? got : other;
	}
	
	/**
	 * Return the value of the {@link Entity} reference if it is set and can be found. If the
	 * reference has not yet been evaluated, it will search the given {@link Level} for it.
	 * If it is not found, it will return, it will return <code>other</code>.
	 *
	 * @param level The {@link Level} to search for the {@link Entity} in.
	 * @param other the value to be returned if there is no value present, may be
	 *              <code>null</code>.
	 * @return The {@link Entity} if one was found, or else <code>other</code>.
	 */
	public T orElse(Level level, T other) {
		if (uuid == null) return other;
		T got = get(level);
		return got != null ? got : other;
	}
	
	public class EntityReferenceTypeAdapter extends TypeAdapter<EntityReference> {
		@Override
		public void write(JsonWriter out, EntityReference value) throws IOException {
			out.value(value.uuid.toString());
		}
		
		@Override
		public EntityReference read(JsonReader in) throws IOException {
			UUID uuid = UUID.fromString(in.nextString());
			return new EntityReference(uuid);
		}
	}
}
