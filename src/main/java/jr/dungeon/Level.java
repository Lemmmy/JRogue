package jr.dungeon;

import jr.JRogue;
import jr.dungeon.generators.Climate;
import jr.dungeon.generators.DungeonGenerator;
import jr.dungeon.tiles.Tile;
import jr.utils.*;
import org.json.JSONException;
import org.json.JSONObject;
import jr.ErrorHandler;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Level implements Serialisable, Persisting, Closeable {
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
		lightStore = new LightStore(this);
		visibilityStore = new VisibilityStore(this);
		monsterSpawner = new MonsterSpawner(this);
		
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
		getLightStore().serialise(obj);
		getMonsterSpawner().serialise(obj);
		
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
			getLightStore().unserialise(obj);
			getMonsterSpawner().unserialise(obj);
		} catch (JSONException e) {
			JRogue.getLogger().error("Error loading level:");
			JRogue.getLogger().error(e);
		}
		
		dungeon.entityAdded(dungeon.getPlayer());

		unserialisePersistence(obj);
	}
		
	public UUID getUUID() {
		return uuid;
	}
	
	public Dungeon getDungeon() {
		return dungeon;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public int getSpawnX() {
		return spawnX;
	}
	
	public int getSpawnY() {
		return spawnY;
	}
	
	public void setSpawnPoint(int x, int y) {
		spawnX = x;
		spawnY = y;
	}
	
	public Climate getClimate() {
		return climate;
	}
	
	public TileStore getTileStore() {
		return tileStore;
	}
	
	public EntityStore getEntityStore() {
		return entityStore;
	}
	
	public LightStore getLightStore() {
		return lightStore;
	}
	
	public VisibilityStore getVisibilityStore() {
		return visibilityStore;
	}
	
	public MonsterSpawner getMonsterSpawner() {
		return monsterSpawner;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	@Override
	public JSONObject getPersistence() {
		return persistence;
	}
	
	@Override
	public void close() {
		getTileStore().close();
	}
}
