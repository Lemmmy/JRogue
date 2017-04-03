package jr.dungeon;

import jr.ErrorHandler;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.entities.events.EntityRemovedEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.player.Player;
import jr.utils.Point;
import jr.utils.Serialisable;
import jr.utils.Utils;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Store class for {@link Entity Entities}. Handles storage, serialisation, unserialisation, getters and setters
 * related to {@link Entity Entities}.
 */
public class EntityStore implements Serialisable {
	/**
	 * The map of {@link Entity Entities} in the {@link Level}.
	 */
	private Map<UUID, Entity> entities;
	
	/**
	 * Queue of {@link Entity Entities} that are being added. Queues are processed and flushed at the start and end
	 * of every turn.
	 */
	@Getter private List<Entity> entityAddQueue;
	/**
	 * Queue of {@link Entity Entities} that are being removed. Queues are processed and flushed at the start and end
	 * of every turn.
	 */
	@Getter private List<Entity> entityRemoveQueue;
	
	/**
	 * The {@link Dungeon} related to this EntityStore.
	 */
	private Dungeon dungeon;
	/**
	 * The {@link Level} this EntityStore is storing {@link Entity Entities} for.
	 */
	private Level level;
	
	/**
	 * @param level The {@link Level} this EntityStore is storing {@link Entity Entities} for.
	 */
	public EntityStore(Level level) {
		this.dungeon = level.getDungeon();
		this.level = level;
		
		entities = new HashMap<>();
		entityAddQueue = new ArrayList<>();
		entityRemoveQueue = new ArrayList<>();
	}
	
	@Override
	public void serialise(JSONObject obj) {
		entities.values().forEach(e -> {
			JSONObject serialisedEntity = new JSONObject();
			e.serialise(serialisedEntity);
			obj.append("entities", serialisedEntity);
		});
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		JSONArray serialisedEntities = obj.getJSONArray("entities");
		serialisedEntities.forEach(serialisedEntity -> unserialiseEntity((JSONObject) serialisedEntity));
	}
	
	@SuppressWarnings("unchecked")
	private void unserialiseEntity(JSONObject serialisedEntity) {
		String entityClassName = serialisedEntity.getString("class");
		int x = serialisedEntity.getInt("x");
		int y = serialisedEntity.getInt("y");
		
		try {
			Class<? extends Entity> entityClass = (Class<? extends Entity>) Class.forName(entityClassName);
			Constructor<? extends Entity> entityConstructor = entityClass.getConstructor(
				Dungeon.class,
				Level.class,
				int.class,
				int.class
			);
			
			Entity entity = entityConstructor.newInstance(dungeon, level, x, y);
			entity.unserialise(serialisedEntity);
			addEntity(entity);
			
			if (entity instanceof Player) {
				dungeon.setPlayer((Player) entity);
			}
		} catch (ClassNotFoundException e) {
			ErrorHandler.error("Unknown entity class " + entityClassName, e);
		} catch (NoSuchMethodException e) {
			ErrorHandler.error("Entity class has no unserialisation constructor " + entityClassName, e);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			ErrorHandler.error("Error loading entity class " + entityClassName, e);
		}
	}
	
	/**
	 * Goes through the {@link #entityAddQueue} and {@link #entityRemoveQueue}, adding new {@link Entity Entities} to
	 * the level and store, and removing ones that should be removed. Queues are processed at the start and end of
	 * every turn. Queue processing triggers {@link EntityAddedEvent}s and {@link EntityRemovedEvent}s.
	 */
	public void processEntityQueues(boolean isNew) {
		for (Iterator<Entity> iterator = entityAddQueue.iterator(); iterator.hasNext(); ) {
			Entity entity = iterator.next();
			entities.put(entity.getUUID(), entity);
			dungeon.triggerEvent(new EntityAddedEvent(entity, isNew));
			iterator.remove();
		}
		
		for (Iterator<Entity> iterator = entityRemoveQueue.iterator(); iterator.hasNext(); ) {
			Entity entity = iterator.next();
			entities.remove(entity.getUUID());
			dungeon.triggerEvent(new EntityRemovedEvent(entity));
			iterator.remove();
		}
	}
	
	/**
	 * @return All {@link Entity Entities} in the store.
	 */
	public Collection<Entity> getEntities() {
		return entities.values();
	}
	
	/**
	 * @param uuid The UUID to search by, as a {@link UUID} object.
	 *
	 * @return The {@link Entity} associated with this UUID.
	 */
	public Entity getEntityByUUID(UUID uuid) {
		return entities.get(uuid);
	}
	
	/**
	 * @param uuid The UUID to search by, as a <tt>String</tt>.
	 *
	 * @return The {@link Entity} associated with this UUID.
	 */
	public Entity getEntityByUUID(String uuid) {
		return entities.get(UUID.fromString(uuid));
	}
	
	/**
	 * @param x The X position to check.
	 * @param y The Y position to check.
	 *
	 * @return All {@link Entity Entities} at the specified location.
	 */
	public List<Entity> getEntitiesAt(int x, int y) {
		return entities.values().stream()
			.filter(e -> e.getX() == x && e.getY() == y)
			.collect(Collectors.toList());
	}
	
	/**
	 * @param p The position to check.
	 *
	 * @return All {@link Entity Entities} at the specified location.
	 */
	public List<Entity> getEntitiesAt(Point p) {
		return entities.values().stream()
			.filter(e -> e.getPosition().equals(p))
			.collect(Collectors.toList());
	}
	
	/**
	 * @return All {@link Monster Monster entities} in the store.
	 */
	public List<Entity> getMonsters() {
		return entities.values().stream()
			.filter(Monster.class::isInstance)
			.collect(Collectors.toList());
	}
	
	/**
	 * @return All {@link Monster#isHostile() hostile} {@link Monster Monster entities} in the store.
	 */
	public List<Entity> getHostileMonsters() {
		return entities.values().stream()
			.filter(Monster.class::isInstance)
			.filter(e -> ((Monster) e).isHostile())
			.collect(Collectors.toList());
	}
	
	/**
	 * @param x The X position to check.
	 * @param y The Y position to check.
	 *
	 * @return All {@link Entity Entities} adjacent to this tile.
	 *
	 * @see Utils#DIRECTIONS
	 */
	public List<Entity> getAdjacentEntities(int x, int y) {
		List<Entity> entities = new ArrayList<>();
		
		Arrays.stream(Utils.DIRECTIONS).forEach(d -> entities.addAll(getEntitiesAt(x + d.getX(), y + d.getY())));
		
		return entities;
	}
	
	/**
	 * @param x The X position to check.
	 * @param y The Y position to check.
	 *
	 * @return All {@link Monster Monster entities} adjacent to this tile.
	 *
	 * @see Utils#DIRECTIONS
	 */
	public List<Entity> getAdjacentMonsters(int x, int y) {
		return getAdjacentEntities(x, y).stream()
			.filter(e -> e instanceof Monster)
			.collect(Collectors.toList());
	}
	
	/**
	 * @param x The X position to check.
	 * @param y The Y position to check.
	 *
	 * @return All {@link Entity Entities} at the specified location that
	 * {@link Entity#canBeWalkedOn() cannot be walked on}.
	 */
	public List<Entity> getUnwalkableEntitiesAt(int x, int y) {
		return entities.values().stream()
			.filter(e -> e.getX() == x && e.getY() == y && !e.canBeWalkedOn())
			.collect(Collectors.toList());
	}
	
	
	/**
	 * @param x The X position to check.
	 * @param y The Y position to check.
	 *
	 * @return All {@link Entity Entities} at the specified location that
	 * {@link Entity#canBeWalkedOn() can be walked on}.
	 */
	public List<Entity> getWalkableEntitiesAt(int x, int y) {
		return entities.values().stream()
			.filter(e -> e.getX() == x && e.getY() == y && e.canBeWalkedOn())
			.collect(Collectors.toList());
	}
	
	/**
	 * Adds an {@link Entity} to the {@link #entityAddQueue}. It will be added to the store when the queues are
	 * {@link #processEntityQueues(boolean) next processed}.
	 *
	 * @param entity The {@link Entity} to add.
	 *
	 * @return <tt>true</tt> if the collection changed as a result of the call, as per {@link Collection#add(Object)}.
	 */
	public boolean addEntity(Entity entity) {
		return entityAddQueue.add(entity);
	}
	
	/**
	 * Adds an {@link Entity} to the {@link #entityRemoveQueue}. It will be removed from the store when the queues are
	 * {@link #processEntityQueues(boolean) next processed}. The {@link Entity}'s {@link Entity#beingRemoved} flag will be
	 * assigned instantly, so the {@link Entity} knows it will be removed from the store/{@link Level} when the queues
	 * are {@link #processEntityQueues(boolean) next processed}.
	 *
	 * @param entity The {@link Entity} to remove.
	 *
	 * @return <tt>true</tt> if the collection changed as a result of the call, as per {@link Collection#add(Object)}.
	 */
	public boolean removeEntity(Entity entity) {
		entity.setBeingRemoved(true);
		return entityRemoveQueue.add(entity);
	}
}