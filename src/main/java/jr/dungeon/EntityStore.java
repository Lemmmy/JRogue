package jr.dungeon;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.entities.events.EntityRemovedEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.utils.Directions;
import jr.utils.Point;
import lombok.Getter;

import java.util.*;
import java.util.stream.Stream;

/**
 * Store class for {@link Entity Entities}. Handles storage, serialisation, deserialisation, getters and setters
 * related to {@link Entity Entities}.
 */
public class EntityStore {
    /**
     * The map of {@link Entity Entities} in the {@link Level}.
     */
    @Expose private Map<UUID, Entity> entities;
    
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
        this.level = level;
    }
    
    public void initialise() {
        dungeon = level.getDungeon();
        
        entities = new HashMap<>();
        entityAddQueue = new ArrayList<>();
        entityRemoveQueue = new ArrayList<>();
    }
    
    public void setLevel(Level level) {
        this.dungeon = level.getDungeon();
        this.level = level;
        
        this.getEntities().forEach(entity -> entity.setLevelInternal(level));
        
        entityAddQueue = new ArrayList<>();
        entityRemoveQueue = new ArrayList<>();
    }
    
    /**
     * Goes through the {@link #entityAddQueue} and {@link #entityRemoveQueue}, adding new {@link Entity Entities} to
     * the level and store, and removing ones that should be removed. Queues are processed at the start and end of
     * every turn. Queue processing triggers {@link EntityAddedEvent}s and {@link EntityRemovedEvent}s.
     *
     * @param isNew Whether or not the entities were created, and not just spawned from a new level or deserialisation.
     */
    public void processEntityQueues(boolean isNew) {
        for (Iterator<Entity> iterator = entityAddQueue.iterator(); iterator.hasNext(); ) {
            Entity entity = iterator.next();
            entities.put(entity.getUUID(), entity);
            dungeon.eventSystem.triggerEvent(new EntityAddedEvent(entity, isNew));
            iterator.remove();
        }
        
        for (Iterator<Entity> iterator = entityRemoveQueue.iterator(); iterator.hasNext(); ) {
            Entity entity = iterator.next();
            entities.remove(entity.getUUID());
            dungeon.eventSystem.triggerEvent(new EntityRemovedEvent(entity));
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
     * @param uuid The UUID to search by, as a {@code String}.
     *
     * @return The {@link Entity} associated with this UUID.
     */
    public Entity getEntityByUUID(String uuid) {
        return entities.get(UUID.fromString(uuid));
    }
    
    /**
     * @param point The position to check.
     *
     * @return All {@link Entity Entities} at the specified location.
     */
    public Stream<Entity> getEntitiesAt(Point point) {
        return entities.values().stream()
            .filter(e -> e.getPosition().equals(point));
    }
    
    public boolean areEntitiesAt(Point point) {
        return getEntitiesAt(point).findAny().isPresent();
    }
    
    /**
     * @return All {@link Monster Monster entities} in the store.
     */
    public Stream<Monster> getMonsters() {
        return entities.values().stream()
            .filter(Monster.class::isInstance)
            .map(Monster.class::cast);
    }
    
    /**
     * @return All {@link Monster#isHostile() hostile} {@link Monster Monster entities} in the store.
     */
    public Stream<Monster> getHostileMonsters() {
        return getMonsters().filter(Monster::isHostile);
    }
    
    /**
     * @param point The position to check.
     *
     * @return All {@link EntityItem EntityItems} at the specified location.
     */
    public Stream<EntityItem> getItemsAt(Point point) {
        return getEntitiesAt(point)
            .filter(EntityItem.class::isInstance)
            .map(EntityItem.class::cast);
    }
    
    /**
     * @param point The position to check.
     *
     * @return All {@link Entity Entities} cardinally adjacent to this tile.
     *
     * @see Directions#CARDINAL
     */
    public Stream<Entity> getAdjacentEntities(Point point) {
        return Directions.cardinal()
            .map(point::add)
            .flatMap(this::getEntitiesAt);
    }
    
    /**
     * @param point The position to check.
     *
     * @return All {@link Monster Monster entities} adjacent to this tile.
     *
     * @see Directions#CARDINAL
     */
    public Stream<Entity> getAdjacentMonsters(Point point) {
        return getAdjacentEntities(point)
            .filter(Monster.class::isInstance);
    }
    
    /**
     * @param point The position to check.
     *
     * @return All {@link Entity Entities} at the specified location that
     * {@link Entity#canBeWalkedOn() cannot be walked on}.
     */
    public Stream<Entity> getUnwalkableEntitiesAt(Point point) {
        return getEntitiesAt(point).filter(e -> !e.canBeWalkedOn());
    }
    
    /**
     * @param point The position to check.
     *
     * @return All {@link Entity Entities} at the specified location that
     * {@link Entity#canBeWalkedOn() can be walked on}.
     */
    public Stream<Entity> getWalkableEntitiesAt(Point point) {
        return getEntitiesAt(point).filter(Entity::canBeWalkedOn);
    }
    
    /**
     * Adds an {@link Entity} to the {@link #entityAddQueue}. It will be added to the store when the queues are
     * {@link #processEntityQueues(boolean) next processed}.
     *
     * @param entity The {@link Entity} to add.
     *
     * @return {@code true} if the collection changed as a result of the call, as per {@link Collection#add(Object)}.
     */
    public boolean addEntity(Entity entity) {
        return entityAddQueue.add(entity);
    }
    
    /**
     * Adds an {@link Entity} to the {@link #entityRemoveQueue}. It will be removed from the store when the queues are
     * {@link #processEntityQueues(boolean) next processed}. The {@link Entity}'s {@link Entity#isBeingRemoved()} flag
     * will be assigned instantly, so the {@link Entity} knows it will be removed from the store/{@link Level} when the
     * queues are {@link #processEntityQueues(boolean) next processed}.
     *
     * @param entity The {@link Entity} to remove.
     *
     * @return {@code true} if the collection changed as a result of the call, as per {@link Collection#add(Object)}.
     */
    public boolean removeEntity(Entity entity) {
        entity.setBeingRemoved(true);
        return entityRemoveQueue.add(entity);
    }
    
    public boolean hasEntity(Entity entity) {
        return entities.containsValue(entity);
    }
}