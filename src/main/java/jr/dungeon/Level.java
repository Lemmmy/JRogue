package jr.dungeon;

import jr.JRogue;
import jr.dungeon.generators.Climate;
import jr.dungeon.generators.DungeonGenerator;
import jr.dungeon.tiles.Tile;
import jr.utils.*;
import org.json.JSONException;
import org.json.JSONObject;
import jr.ErrorHandler;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.dungeon.tiles.TileType;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class Level implements Serialisable, Persisting, Closeable {
	private static final int LIGHT_MAX_LIGHT_LEVEL = 100;
	private static final int LIGHT_ABSOLUTE = 80;
	
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
				
				buildLight(true);
				
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
		
		getTileStore().serialise(obj);
		getEntityStore().serialise(obj);
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
			
			getTileStore().serialise(obj);
			getEntityStore().unserialise(obj);
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
	
	public MonsterSpawner getMonsterSpawner() {
		return monsterSpawner;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Color getAmbientLight() {
		return Color.WHITE;
	}
	
	public int getAmbientLightIntensity() {
		return 20;
	}
	
	public Color applyIntensity(Color colour, int intensity) {
		float k;
		
		k = intensity >= LIGHT_ABSOLUTE ? 1 : (float) intensity / (float) LIGHT_ABSOLUTE;
		
		return new Color(
			(int) (colour.getRed() * k),
			(int) (colour.getGreen() * k),
			(int) (colour.getBlue() * k),
			255
		);
	}
	
	public void buildLight(boolean isInitial) {
		resetLight();
		
		for (Tile tile : tiles) {
			int index = tile.getLightIntensity() - 1;
			
			if (index < 0) { continue; }
			if (index >= LIGHT_MAX_LIGHT_LEVEL) { continue; }
			
			lightTiles.get(index).add(tile);
		}
		
		Stream.concat(getEntityStore().getEntities().stream(), getEntityStore().getEntityAddQueue().stream())
			.filter(e -> e instanceof LightEmitter)
			.forEach(e -> {
				LightEmitter lightEmitter = (LightEmitter) e;
				int index = lightEmitter.getLightIntensity() - 1;
				
				if (index < 0 || index >= LIGHT_MAX_LIGHT_LEVEL) { return; }
				
				Tile tile = Tile.getTile(this, TileType.TILE_DUMMY, e.getX(), e.getY());
				
				if (!getTileStore().isTileInvisible(tile.getX(), tile.getY()) && !isInitial) {
					tile.setLightColour(lightEmitter.getLightColour());
					tile.setLightIntensity(lightEmitter.getLightIntensity());
				}
				
				lightTiles.get(index).add(tile);
			});
		
		for (int i = LIGHT_MAX_LIGHT_LEVEL - 1; i >= 0; i--) {
			List<Tile> lights = lightTiles.get(i);
			
			//noinspection ForLoopReplaceableByForEach
			for (int j = 0; j < lights.size(); j++) {
				Tile tile = lights.get(j);
				
				if (tile.getLightIntensity() != i + 1) { continue; }
				
				propagateLighting(tile, isInitial);
			}
		}
	}
	
	public void resetLight() {
		lightTiles = new ArrayList<>();
		
		for (int i = 0; i < LIGHT_MAX_LIGHT_LEVEL; i++) {
			lightTiles.add(i, new ArrayList<>());
		}
		
		Arrays.stream(tiles)
			.filter(t -> !getTileStore().isTileInvisible(t.getX(), t.getY()))
			.forEach(Tile::resetLight);
	}
	
	public void propagateLighting(Tile tile, boolean isInitial) {
		int x = tile.getX();
		int y = tile.getY();
		
		int intensity = tile.getLightIntensity() - tile.getAbsorb();
		
		if (intensity < 0) {
			return;
		}
		
		Color colour = reapplyIntensity(tile.getLightColour(), tile.getLightIntensity(), intensity);
		
		if (x > 0) { setIntensity(getTileStore().getTile(x - 1, y), intensity, colour, isInitial); }
		if (x < getWidth() - 1) { setIntensity(getTileStore().getTile(x + 1, y), intensity, colour, isInitial); }
		if (y > 0) { setIntensity(getTileStore().getTile(x, y - 1), intensity, colour, isInitial); }
		if (y < getHeight() - 1) { setIntensity(getTileStore().getTile(x, y + 1), intensity, colour, isInitial); }
		
		colour = new Color(
			(int) (colour.getRed() * 0.9f),
			(int) (colour.getGreen() * 0.9f),
			(int) (colour.getBlue() * 0.9f),
			colour.getAlpha()
		);
		
		if (x > 0 && y < getWidth() - 1) { setIntensity(getTileStore().getTile(x - 1, y + 1), intensity, colour, isInitial); }
		if (x < getWidth() - 1 && y > 0) { setIntensity(getTileStore().getTile(x + 1, y - 1), intensity, colour, isInitial); }
		if (x > 0 && y < 0) { setIntensity(getTileStore().getTile(x - 1, y - 1), intensity, colour, isInitial); }
		if (x < getWidth() - 1 && y < getHeight() - 1) {
			setIntensity(getTileStore().getTile(x + 1, y + 1), intensity, colour, isInitial);
		}
	}
	
	public Color reapplyIntensity(Color colour, int intensityOld, int intensityNew) {
		float k1, k2;
		
		k1 = intensityNew >= LIGHT_ABSOLUTE ? 1 : (float) intensityNew / (float) LIGHT_ABSOLUTE;
		k2 = intensityOld >= LIGHT_ABSOLUTE ? 1 : (float) intensityOld / (float) LIGHT_ABSOLUTE;
		
		return new Color(
			(int) Math.min(255, colour.getRed() * k1 / k2),
			(int) Math.min(255, colour.getGreen() * k1 / k2),
			(int) Math.min(255, colour.getBlue() * k1 / k2),
			255
		);
	}
	
	public void setIntensity(Tile tile, int intensity, Color colour, boolean isInitial) {
		if (tile == null || getTileStore().isTileInvisible(tile.getX(), tile.getY()) && !isInitial) {
			return;
		}
		
		if (intensity > tile.getLightIntensity() || canMixColours(tile.getLightColour(), colour)) {
			tile.setLightColour(mixColours(tile.getLightColour(), colour));
			
			if (intensity != tile.getLightIntensity()) {
				tile.setLightIntensity(intensity);
			}
			
			int index = tile.getLightIntensity() - 1;
			
			if (index < 0) { return; }
			if (index >= LIGHT_MAX_LIGHT_LEVEL) { return; }
			
			lightTiles.get(index).add(tile);
		}
	}
	
	public boolean canMixColours(Color base, Color light) {
		return light.getRed() > base.getRed() ||
			light.getGreen() > base.getGreen() ||
			light.getBlue() > base.getBlue();
	}
	
	public Color mixColours(Color c1, Color c2) {
		return new Color(
			c1.getRed() > c2.getRed() ? c1.getRed() : c2.getRed(),
			c1.getGreen() > c2.getGreen() ? c1.getGreen() : c2.getGreen(),
			c1.getBlue() > c2.getBlue() ? c1.getBlue() : c2.getBlue(),
			255
		);
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
