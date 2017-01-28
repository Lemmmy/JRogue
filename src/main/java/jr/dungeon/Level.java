package jr.dungeon;

import jr.ErrorHandler;
import jr.JRogue;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.generators.Climate;
import jr.dungeon.generators.DungeonGenerator;
import jr.dungeon.tiles.Tile;
import jr.utils.Persisting;
import jr.utils.Serialisable;
import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.UUID;

@Getter
public class Level implements Serialisable, Persisting {
	private UUID uuid;
	
	private Dungeon dungeon;
	
	private Climate climate;
	
	private int width;
	private int height;
	private int depth;
	
	private int spawnX;
	private int spawnY;
	
	private TileStore tileStore;
	private EntityStore entityStore;
	private LightStore lightStore;
	private VisibilityStore visibilityStore;
	private MonsterSpawner monsterSpawner;
	
	private JSONObject persistence;
	
	public Level(Dungeon dungeon, int width, int height, int depth) {
		this(UUID.randomUUID(), dungeon, width, height, depth);
	}
	
	public Level(UUID uuid, Dungeon dungeon, int width, int height, int depth) {
		this.uuid = uuid;
		
		this.dungeon = dungeon;
		
		this.width = width;
		this.height = height;
		
		this.depth = depth;
	}
	
	private void initialise() {
		tileStore = new TileStore(this);
		entityStore = new EntityStore(this);
		visibilityStore = new VisibilityStore(this);
		lightStore = new LightStore(this);
		monsterSpawner = new MonsterSpawner(this);

		lightStore.initialiseLight();

		persistence = new JSONObject();
	}
	
	protected void generate(Tile sourceTile, Class<? extends DungeonGenerator> generatorClass) {
		boolean gotLevel = false;
		
		do {
			initialise();
			
			try {
				Constructor generatorConstructor = generatorClass.getConstructor(Level.class, Tile.class);
				DungeonGenerator generator = (DungeonGenerator) generatorConstructor.newInstance(this, sourceTile);
				
				if (!generator.generate()) {
					continue;
				}
				
				climate = generator.getClimate();
				getMonsterSpawner().setMonsterSpawningStrategy(generator.getMonsterSpawningStrategy());
				
				getLightStore().buildLight(true);
				
				gotLevel = true;
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
				ErrorHandler.error("Error generating level", e);
			}
		} while (!gotLevel);
		
		getMonsterSpawner().spawnMonsters();
	}
	
	public static Optional<Level> createFromJSON(UUID uuid, JSONObject obj, Dungeon dungeon) {
		try {
			int width = obj.getInt("width");
			int height = obj.getInt("height");
			int depth = obj.getInt("depth");
			
			Level level = new Level(uuid, dungeon, width, height, depth);
			level.unserialise(obj);
			return Optional.of(level);
		} catch (JSONException e) {
			JRogue.getLogger().error("Error loading level:");
			JRogue.getLogger().error(e);
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
			
			tileStore.unserialise(obj);
			entityStore.unserialise(obj);
			lightStore.unserialise(obj);
			visibilityStore.unserialise(obj);
			monsterSpawner.unserialise(obj);
		} catch (JSONException e) {
			JRogue.getLogger().error("Error loading level:");
			JRogue.getLogger().error(e);
		}
		
		dungeon.triggerEvent(new EntityAddedEvent(dungeon.getPlayer()));

		unserialisePersistence(obj);
	}
	
	public void setSpawnPoint(int x, int y) {
		spawnX = x;
		spawnY = y;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	@Override
	public JSONObject getPersistence() {
		return persistence;
	}
}
