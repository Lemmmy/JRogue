package jr.dungeon;

import jr.ErrorHandler;
import jr.JRogue;
import jr.debugger.utils.Debuggable;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.generators.Climate;
import jr.dungeon.generators.DungeonGenerator;
import jr.dungeon.tiles.Tile;
import jr.utils.Persisting;
import jr.utils.Serialisable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.UUID;

@Getter
public class Level implements Serialisable, Persisting, Debuggable {
	private UUID uuid;
	
	private Dungeon dungeon;
	
	private Climate climate;
	
	private int width;
	private int height;
	private int depth;
	
	private int spawnX;
	private int spawnY;
	
	private long turnCreated;
	
	@Setter private String name;
	
	@Getter(AccessLevel.NONE) public final TileStore tileStore;
	@Getter(AccessLevel.NONE) public final EntityStore entityStore;
	@Getter(AccessLevel.NONE) public final LightStore lightStore;
	@Getter(AccessLevel.NONE) public final VisibilityStore visibilityStore;
	@Getter(AccessLevel.NONE) public final MonsterSpawner monsterSpawner;
	
	private JSONObject persistence;

	/**
	 * Constructs a level with a random UUID.
	 * @param dungeon The dungeon this level should belong to.
	 * @param width The width of this level in tiles.
	 * @param height The height of this level in tiles.
	 * @param depth The depth this level is at.
	 */
	public Level(Dungeon dungeon, int width, int height, int depth) {
		this(UUID.randomUUID(), dungeon, width, height, depth);
	}

	/**
	 * Constructs a level with a specified UUID.
	 * @param uuid The UUID to use for this level.
	 * @param dungeon The dungeon this level should belong to.
	 * @param width The width of this level in tiles.
	 * @param height The height of this level in tiles.
	 * @param depth The depth this level is at.
	 */
	public Level(UUID uuid, Dungeon dungeon, int width, int height, int depth) {
		this.uuid = uuid;
		
		this.dungeon = dungeon;
		
		this.width = width;
		this.height = height;
		
		this.depth = depth;
		
		tileStore = new TileStore();
		entityStore = new EntityStore(this);
		visibilityStore = new VisibilityStore(this);
		lightStore = new LightStore(this);
		monsterSpawner = new MonsterSpawner(this);
	}

	/**
	 * Initialises the Level, including the initialisation of its stores.
	 */
	private void initialise() {
		tileStore.initialise(this);
		entityStore.initialise();
		visibilityStore.initialise();
		lightStore.initialise();
		monsterSpawner.initialise();
		
		lightStore.initialiseLight();
		
		turnCreated = dungeon.turnSystem.getTurn();

		persistence = new JSONObject();
	}

	/**
	 * Generates the level.
	 * @param sourceTile The tile the player entered the level from. Usually a staircase.
	 * @param generatorClass The {@link jr.dungeon.generators.DungeonGenerator} to generate the level with.
	 */
	protected void generate(Tile sourceTile, Class<? extends DungeonGenerator> generatorClass) {
		boolean gotLevel = false;
		
		tileStore.setEventsSuppressed(true);
		
		do {
			initialise();
			
			try {
				Constructor generatorConstructor = generatorClass.getConstructor(Level.class, Tile.class);
				DungeonGenerator generator = (DungeonGenerator) generatorConstructor.newInstance(this, sourceTile);
				
				if (!generator.generate()) {
					continue;
				}
				
				climate = generator.getClimate();
				monsterSpawner.setMonsterSpawningStrategy(generator.getMonsterSpawningStrategy());
				
				lightStore.buildLight(true);
				
				gotLevel = true;
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
				ErrorHandler.error("Error generating level", e);
			}
		} while (!gotLevel);
		
		tileStore.setEventsSuppressed(false);
		
		monsterSpawner.spawnMonsters();
	}

	/**
	 * Unserialises the level from a JSONObject.
	 * @param uuid The UUID to give to the unserialised level.
	 * @param obj The JSONObject containing the serialised level.
	 * @param dungeon The {@link jr.dungeon.Dungeon} this level should belong to.
	 * @return The unserialised level.
	 */
	public static Optional<Level> createFromJSON(UUID uuid, JSONObject obj, Dungeon dungeon) {
		Level level = null;
		
		try {
			int width = obj.getInt("width");
			int height = obj.getInt("height");
			int depth = obj.getInt("depth");
			
			level = new Level(uuid, dungeon, width, height, depth);
			level.unserialise(obj);
			return Optional.of(level);
		} catch (Exception e) {
			if (level != null) {
				JRogue.getLogger().error("Error loading level " + level.toString() + ":", e);
			} else {
				JRogue.getLogger().error("Error loading level:", e);
			}
		}
		
		return Optional.empty();
	}

	@Override
	public void serialise(JSONObject obj) {
		obj.put("width", getWidth());
		obj.put("height", getHeight());
		obj.put("depth", getDepth());
		obj.put("spawnX", getSpawnX());
		obj.put("spawnY", getSpawnY());
		obj.put("climate", getClimate().name());
		obj.put("turnCreated", turnCreated);
		obj.put("name", name);
		
		tileStore.serialise(obj);
		entityStore.serialise(obj);
		lightStore.serialise(obj);
		visibilityStore.serialise(obj);
		monsterSpawner.serialise(obj);
		
		serialisePersistence(obj);
	}

	@Override
	public void unserialise(JSONObject obj) {
		initialise();
		
		try {
			spawnX = obj.getInt("spawnX");
			spawnY = obj.getInt("spawnY");
			
			climate = Climate.valueOf(obj.optString("climate", Climate.WARM.name()));
			
			turnCreated = obj.optInt("turnCreated", 0);
			name = obj.optString("name", "Dungeon");
			
			tileStore.unserialise(obj);
			entityStore.unserialise(obj);
			lightStore.unserialise(obj);
			visibilityStore.unserialise(obj);
			monsterSpawner.unserialise(obj);
		} catch (JSONException e) {
			JRogue.getLogger().error("Error loading level:", e);
		}
		
		dungeon.eventSystem.triggerEvent(new EntityAddedEvent(dungeon.getPlayer(), false));

		unserialisePersistence(obj);
	}

	/**
	 * Sets the level's spawn point in tile coordinates. This is where the player will appear when entering the level.
	 * @param x The x coordinate of the spawn point.
	 * @param y The y coordinate of the spawn point.
	 */
	public void setSpawnPoint(int x, int y) {
		spawnX = x;
		spawnY = y;
	}

	/**
	 * @return A UUID unique to this level.
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * @return A JSONObject containing data that will persist across game sessions.
	 */
	@Override
	public JSONObject getPersistence() {
		return persistence;
	}
	
	@Override
	public String toString() {
		return String.format("%s %,d", name, depth);
	}
	
	@Override
	public String getValueHint() {
		return String.format(
			"[P_GREY_3]%s[] %,d",
			name, depth
		);
	}
}
