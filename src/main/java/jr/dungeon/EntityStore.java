package jr.dungeon;

import jr.ErrorHandler;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.entities.events.EntityRemovedEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.player.Player;
import jr.utils.Serialisable;
import jr.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class EntityStore implements Serialisable {
	private Map<UUID, Entity> entities;
	
	private List<Entity> entityAddQueue;
	private List<Entity> entityRemoveQueue;
	
	private Dungeon dungeon;
	private Level level;
	
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
	
	public void processEntityQueues() {
		for (Iterator<Entity> iterator = entityAddQueue.iterator(); iterator.hasNext(); ) {
			Entity entity = iterator.next();
			entities.put(entity.getUUID(), entity);
			dungeon.triggerEvent(new EntityAddedEvent(entity));
			iterator.remove();
		}
		
		for (Iterator<Entity> iterator = entityRemoveQueue.iterator(); iterator.hasNext(); ) {
			Entity entity = iterator.next();
			entities.remove(entity.getUUID());
			dungeon.triggerEvent(new EntityRemovedEvent(entity));
			iterator.remove();
		}
	}
	
	public Collection<Entity> getEntities() {
		return entities.values();
	}
	
	public List<Entity> getEntityAddQueue() {
		return entityAddQueue;
	}
	
	public List<Entity> getEntityRemoveQueue() {
		return entityRemoveQueue;
	}
	
	public Entity getEntityByUUID(UUID uuid) {
		return entities.get(uuid);
	}
	
	public Entity getEntityByUUID(String uuid) {
		return entities.get(UUID.fromString(uuid));
	}
	
	public List<Entity> getEntitiesAt(int x, int y) {
		return entities.values().stream()
			.filter(e -> e.getX() == x && e.getY() == y)
			.collect(Collectors.toList());
	}
	
	public List<Entity> getMonsters() {
		return entities.values().stream()
			.filter(Monster.class::isInstance)
			.collect(Collectors.toList());
	}
	
	public List<Entity> getHostileMonsters() {
		return entities.values().stream()
			.filter(Monster.class::isInstance)
			.filter(e -> ((Monster) e).isHostile())
			.collect(Collectors.toList());
	}
	
	public List<Entity> getAdjacentEntities(int x, int y) {
		List<Entity> entities = new ArrayList<>();
		
		Arrays.stream(Utils.DIRECTIONS).forEach(d -> entities.addAll(getEntitiesAt(x + d[0], y + d[1])));
		
		return entities;
	}
	
	public List<Entity> getAdjacentMonsters(int x, int y) {
		return getAdjacentEntities(x, y).stream()
			.filter(e -> e instanceof Monster)
			.collect(Collectors.toList());
	}
	
	public List<Entity> getUnwalkableEntitiesAt(int x, int y) {
		return entities.values().stream()
			.filter(e -> e.getX() == x && e.getY() == y && !e.canBeWalkedOn())
			.collect(Collectors.toList());
	}
	
	public List<Entity> getWalkableEntitiesAt(int x, int y) {
		return entities.values().stream()
			.filter(e -> e.getX() == x && e.getY() == y && e.canBeWalkedOn())
			.collect(Collectors.toList());
	}
	
	public boolean addEntity(Entity entity) {
		return entityAddQueue.add(entity);
	}
	
	public boolean removeEntity(Entity entity) {
		entity.setBeingRemoved(true);
		return entityRemoveQueue.add(entity);
	}
}