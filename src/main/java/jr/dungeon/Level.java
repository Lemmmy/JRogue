package jr.dungeon;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import jr.ErrorHandler;
import jr.debugger.utils.Debuggable;
import jr.dungeon.generators.Climate;
import jr.dungeon.generators.DungeonGenerator;
import jr.dungeon.serialisation.Serialisable;
import jr.dungeon.tiles.Tile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.UUID;

@Getter
@JsonAdapter(Level.LevelDeserialiser.class)
public class Level implements Serialisable, Debuggable {
	@Expose private UUID uuid;
	
	private Dungeon dungeon;
	
	@Expose private Climate climate;
	
	@Expose private int width;
	@Expose private int height;
	@Expose private int depth;
	
	@Expose private int spawnX;
	@Expose private int spawnY;
	
	@Expose private long turnCreated;
	
	@Expose @Setter private String name;
	
	@Getter(AccessLevel.NONE) public final TileStore tileStore = new TileStore(this);
	@Getter(AccessLevel.NONE) public final VisibilityStore visibilityStore = new VisibilityStore(this);
	@Getter(AccessLevel.NONE) public final LightStore lightStore = new LightStore(this);
	@Expose @Getter(AccessLevel.NONE) public final EntityStore entityStore = new EntityStore(this);
	@Expose @Getter(AccessLevel.NONE) public final MonsterSpawner monsterSpawner = new MonsterSpawner(this);
	
	@Expose @Getter private DungeonGenerator generator;

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
	}

	/**
	 * Initialises the Level, including the initialisation of its stores.
	 */
	public void initialise() {
		tileStore.initialise();
		visibilityStore.initialise();
		lightStore.initialise();
		entityStore.initialise();
		monsterSpawner.initialise();
		
		lightStore.initialiseLight();
		
		turnCreated = dungeon.turnSystem.getTurn();
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
				generator = (DungeonGenerator) generatorConstructor.newInstance(this, sourceTile);
				
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
	
	@Override
	public String toString() {
		return String.format("%s %,d", name, depth);
	}
	
	@Override
	public String getValueString() {
		return String.format(
			"[P_GREY_3]%s[] %,d",
			name, depth
		);
	}
	
	public class LevelDeserialiser implements JsonDeserializer<Level> {
		@Override
		public Level deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject object = json.getAsJsonObject();
			Level level = context.deserialize(json, typeOfT);
			level.afterDeserialise();
			return level;
		}
	}
}
