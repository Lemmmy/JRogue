package pw.lemmmy.jrogue.dungeon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LightEmitter;
import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;
import pw.lemmmy.jrogue.dungeon.entities.monsters.MonsterSpawn;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.generators.Climate;
import pw.lemmmy.jrogue.dungeon.generators.DungeonGenerator;
import pw.lemmmy.jrogue.dungeon.generators.MonsterSpawningStrategy;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.dungeon.tiles.states.TileState;
import pw.lemmmy.jrogue.utils.*;
import pw.lemmmy.jrogue.utils.Point;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Level implements Serialisable, Persisting {
	private static final int LIGHT_MAX_LIGHT_LEVEL = 100;
	private static final int LIGHT_ABSOLUTE = 80;
	
	private static final int MIN_MONSTER_SPAWN_DISTANCE = 15;
	
	private UUID uuid;
	
	private Tile[] tiles;
	
	private Boolean[] discoveredTiles;
	private Boolean[] visibleTiles;
	private List<List<Tile>> lightTiles;
	
	private Dungeon dungeon;
	
	private Climate climate;
	private MonsterSpawningStrategy monsterSpawningStrategy;
	
	private int width;
	private int height;
	private int depth;
	
	private int spawnX;
	private int spawnY;
	
	private Map<UUID, Entity> entities;
	
	private List<Entity> entityAddQueue;
	private List<Entity> entityRemoveQueue;
	
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
		tiles = new Tile[width * height];
		discoveredTiles = new Boolean[width * height];
		visibleTiles = new Boolean[width * height];
		
		for (int i = 0; i < width * height; i++) {
			tiles[i] = new Tile(this, TileType.TILE_GROUND, i % width, (int) Math.floor(i / width));
		}
		
		Arrays.fill(discoveredTiles, false);
		Arrays.fill(visibleTiles, false);
		
		entities = new HashMap<>();
		entityAddQueue = new ArrayList<>();
		entityRemoveQueue = new ArrayList<>();
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
				monsterSpawningStrategy = generator.getMonsterSpawningStrategy();
				
				buildLight(true);
				
				gotLevel = true;
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
				e.printStackTrace();
			}
		} while (!gotLevel);
		
		spawnMonsters();
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
		obj.put("monsterSpawningStrategy", getMonsterSpawningStrategy().name());
		
		serialiseTiles().ifPresent(bytes -> obj.put("tiles", new String(Base64.getEncoder().encode(bytes))));
		
		serialiseLights().ifPresent(bytes -> obj.put("lights", new String(Base64.getEncoder().encode(bytes))));
		
		serialiseBooleanArray(visibleTiles)
			.ifPresent(bytes -> obj.put("visibleTiles", new String(Base64.getEncoder().encode(bytes))));
		
		serialiseBooleanArray(discoveredTiles)
			.ifPresent(bytes -> obj.put("discoveredTiles", new String(Base64.getEncoder().encode(bytes))));
		
		Arrays.stream(tiles).forEach(t -> {
			if (t.hasState()) {
				JSONObject serialisedTileState = new JSONObject();
				serialisedTileState.put("x", t.getX());
				serialisedTileState.put("y", t.getY());
				serialisedTileState.put("class", t.getState().getClass().getName());
				t.getState().serialise(serialisedTileState);
				obj.append("tileStates", serialisedTileState);
			}
		});
		
		entities.values().forEach(e -> {
			JSONObject serialisedEntity = new JSONObject();
			e.serialise(serialisedEntity);
			obj.append("entities", serialisedEntity);
		});

		serialisePersistence(obj);
	}
	
	private Optional<byte[]> serialiseTiles() {
		try (
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos)
		) {
			Arrays.stream(tiles).forEach(t -> {
				try {
					dos.writeShort(t.getType().getID());
				} catch (IOException e) {
					JRogue.getLogger().error("Error saving level:");
					JRogue.getLogger().error(e);
				}
			});
			
			dos.flush();
			
			return Optional.of(bos.toByteArray());
		} catch (IOException e) {
			JRogue.getLogger().error("Error saving level:");
			JRogue.getLogger().error(e);
		}
		
		return Optional.empty();
	}
	
	private Optional<byte[]> serialiseLights() {
		try (
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos)
		) {
			Arrays.stream(tiles).forEach(t -> {
				try {
					dos.writeInt(t.getLightColour().getRGB());
					dos.writeByte(t.getLightIntensity());
				} catch (IOException e) {
					JRogue.getLogger().error("Error saving level:");
					JRogue.getLogger().error(e);
				}
			});
			
			dos.flush();
			
			return Optional.of(bos.toByteArray());
		} catch (IOException e) {
			JRogue.getLogger().error("Error saving level:");
			JRogue.getLogger().error(e);
		}
		
		return Optional.empty();
	}
	
	private Optional<byte[]> serialiseBooleanArray(Boolean[] arr) {
		try (
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos)
		) {
			Arrays.stream(arr).forEach(t -> {
				try {
					dos.writeBoolean(t);
				} catch (IOException e) {
					JRogue.getLogger().error("Error saving level:");
					JRogue.getLogger().error(e);
				}
			});
			
			dos.flush();
			
			return Optional.of(bos.toByteArray());
		} catch (IOException e) {
			JRogue.getLogger().error("Error saving level:");
			JRogue.getLogger().error(e);
		}
		
		return Optional.empty();
	}

	@Override
	public void unserialise(JSONObject obj) {
		initialise();
		
		try {
			spawnX = obj.getInt("spawnX");
			spawnY = obj.getInt("spawnY");
			
			climate = Climate.valueOf(obj.optString("climate", Climate.WARM.name()));
			monsterSpawningStrategy = MonsterSpawningStrategy.valueOf(obj.optString(
				"monsterSpawningStrategy",
				MonsterSpawningStrategy.STANDARD.name()
			));
			
			unserialiseTiles(Base64.getDecoder().decode(obj.getString("tiles")));
			
			unserialiseLights(Base64.getDecoder().decode(obj.getString("lights")));
			
			visibleTiles = unserialiseBooleanArray(
				Base64.getDecoder().decode(obj.getString("visibleTiles")),
				width * height
			);
			
			discoveredTiles = unserialiseBooleanArray(
				Base64.getDecoder().decode(obj.getString("discoveredTiles")),
				width * height
			);
			
			JSONArray entities = obj.getJSONArray("entities");
			entities.forEach(serialisedEntity -> unserialiseEntity((JSONObject) serialisedEntity));
			
			JSONArray serialisedTileStates = obj.getJSONArray("tileStates");
			serialisedTileStates.forEach(serialisedTileState -> unserialiseTileState((JSONObject) serialisedTileState));
		} catch (JSONException e) {
			JRogue.getLogger().error("Error loading level:");
			JRogue.getLogger().error(e);
		}
		
		dungeon.entityAdded(dungeon.getPlayer());

		unserialisePersistence(obj);
	}
	
	private void unserialiseTiles(byte[] bytes) {
		try (
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis)
		) {
			Arrays.stream(tiles).forEach(t -> {
				try {
					short id = dis.readShort();
					TileType type = TileType.fromID(id);
					t.setType(type);
				} catch (IOException e) {
					JRogue.getLogger().error("Error loading level:");
					JRogue.getLogger().error(e);
				}
			});
		} catch (IOException e) {
			JRogue.getLogger().error("Error loading level:");
			JRogue.getLogger().error(e);
		}
	}
	
	private void unserialiseLights(byte[] bytes) {
		try (
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis)
		) {
			Arrays.stream(tiles).forEach(t -> {
				try {
					int colourInt = dis.readInt();
					int intensity = dis.readByte();
					t.setLightColour(new Color(colourInt));
					t.setLightIntensity(intensity);
				} catch (IOException e) {
					JRogue.getLogger().error("Error loading level:");
					JRogue.getLogger().error(e);
				}
			});
		} catch (IOException e) {
			JRogue.getLogger().error("Error loading level:");
			JRogue.getLogger().error(e);
		}
	}
	
	private Boolean[] unserialiseBooleanArray(byte[] bytes, int count) {
		Boolean[] out = new Boolean[count];
		
		try (
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis)
		) {
			for (int i = 0; i < count; i++) {
				out[i] = dis.readBoolean();
			}
		} catch (IOException e) {
			JRogue.getLogger().error("Error loading level:");
			JRogue.getLogger().error(e);
		}
		
		return out;
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
			
			Entity entity = entityConstructor.newInstance(dungeon, this, x, y);
			entity.unserialise(serialisedEntity);
			addEntity(entity);
			
			if (entity instanceof Player) {
				getDungeon().setPlayer((Player) entity);
			}
		} catch (ClassNotFoundException e) {
			JRogue.getLogger().error("Unknown entity class {}", entityClassName);
		} catch (NoSuchMethodException e) {
			JRogue.getLogger().error("Entity class {} has no unserialisation constructor", entityClassName);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			JRogue.getLogger().error("Error loading entity class {}", entityClassName);
			JRogue.getLogger().error(e);
		}
	}
	
	private void unserialiseTileState(JSONObject serialisedTileState) {
		String tileStateClassName = serialisedTileState.getString("class");
		int x = serialisedTileState.getInt("x");
		int y = serialisedTileState.getInt("y");
		
		Tile tile = getTile(x, y);
		
		try {
			@SuppressWarnings("unchecked")
			Class<? extends TileState> tileStateClass = (Class<? extends TileState>) Class.forName(tileStateClassName);
			Constructor<? extends TileState> tileStateConstructor = tileStateClass.getConstructor(Tile.class);
			
			TileState tileState = tileStateConstructor.newInstance(tile);
			tileState.unserialise(serialisedTileState);
			tile.setState(tileState);
		} catch (ClassNotFoundException e) {
			JRogue.getLogger().error("Unknown tile state class {}", tileStateClassName);
		} catch (NoSuchMethodException e) {
			JRogue.getLogger().error("Tile state class {} has no unserialisation constructor", tileStateClassName);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			JRogue.getLogger().error("Error loading tile state class {}", tileStateClassName);
			JRogue.getLogger().error(e);
		}
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
	
	public MonsterSpawningStrategy getMonsterSpawningStrategy() {
		return monsterSpawningStrategy;
	}
	
	public Collection<Entity> getEntities() {
		return entities.values();
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
	
	public void processEntityQueues() {
		for (Iterator<Entity> iterator = entityAddQueue.iterator(); iterator.hasNext(); ) {
			Entity entity = iterator.next();
			entities.put(entity.getUUID(), entity);
			dungeon.entityAdded(entity);
			entity.onSpawn();
			iterator.remove();
		}
		
		for (Iterator<Entity> iterator = entityRemoveQueue.iterator(); iterator.hasNext(); ) {
			Entity entity = iterator.next();
			entities.remove(entity.getUUID());
			dungeon.entityRemoved(entity);
			iterator.remove();
		}
	}
	
	private void spawnMonsters() {
		monsterSpawningStrategy.getSpawns().stream()
			.filter(s -> s.getLevelRange().contains(Math.abs(depth)))
			.forEach(s -> {
				int count = RandomUtils.jrandom(s.getRangePerLevel());
				
				for (int j = 0; j < count; j++) {
					Point point = getMonsterSpawnPoint();
					
					if (s.isPack()) {
						spawnPackAtPoint(s.getMonsterClass(), point, RandomUtils.random(s.getPackRange()));
					} else {
						spawnMonsterAtPoint(s.getMonsterClass(), point);
					}
				}
			});
	}
	
	@SuppressWarnings("unchecked")
	public void spawnNewMonsters() {
		Point point = getMonsterSpawnPointAwayFromPlayer();
		
		if (point != null) {
			List<MonsterSpawn> possibleMonsterSpawns = monsterSpawningStrategy.getSpawns().stream()
				.filter(s -> s.getLevelRange().contains(Math.abs(depth)))
				.collect(Collectors.toList());
			
			MonsterSpawn chosenSpawn = RandomUtils.randomFrom(possibleMonsterSpawns);
			spawnMonsterAtPoint(chosenSpawn.getMonsterClass(), point);
		}
	}
	
	private void spawnMonsterAtPoint(Class<? extends Monster> monsterClass, Point point) {
		try {
			Constructor<? extends Monster> constructor = monsterClass
				.getConstructor(Dungeon.class, Level.class, int.class, int.class);
			
			Entity monster = constructor.newInstance(getDungeon(), this, point.getX(), point.getY());
			addEntity(monster);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			JRogue.getLogger().error("Error spawning monsters", e);
		}
	}
	
	private void spawnPackAtPoint(Class<? extends Monster> monsterClass, Point point, int amount) {
		List<Tile> validTiles = Arrays.stream(tiles)
			.filter(t ->
				t.getType().getSolidity() != TileType.Solidity.SOLID && t.getType().isInnerRoomTile() ||
				t.getType() == TileType.TILE_CORRIDOR
			)
			.sorted(Comparator.comparingInt(a -> Utils.distance(
				point.getX(), point.getY(),
				a.getX(), a.getY()
			)))
			.collect(Collectors.toList());
		
		validTiles.subList(0, amount).forEach(t -> spawnMonsterAtPoint(monsterClass, new Point(t.getX(), t.getY())));
	}
	
	private Point getMonsterSpawnPoint() {
		Tile tile = RandomUtils.randomFrom(Arrays.stream(tiles)
			.filter(t ->
				t.getType().getSolidity() != TileType.Solidity.SOLID && t.getType().isInnerRoomTile() ||
				t.getType() == TileType.TILE_CORRIDOR
			)
			.collect(Collectors.toList())
		);
		
		return new Point(tile.getX(), tile.getY());
	}
	
	private Point getMonsterSpawnPointAwayFromPlayer() {
		Player player = dungeon.getPlayer();
		
		Tile tile = RandomUtils.randomFrom(Arrays.stream(tiles)
			.filter(t ->
				t.getType().getSolidity() != TileType.Solidity.SOLID && t.getType().isInnerRoomTile() ||
				t.getType() == TileType.TILE_CORRIDOR
			)
			.filter(t -> !visibleTiles[width * t.getY() + t.getX()])
			.filter(t -> Utils.distance(
				t.getX(),
				t.getY(),
				player.getX(),
				player.getY()
			) > MIN_MONSTER_SPAWN_DISTANCE)
			.collect(Collectors.toList())
		);
		
		return new Point(tile.getX(), tile.getY());
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Tile[] getTiles() {
		return tiles;
	}
	
	public Boolean[] getDiscoveredTiles() {
		return discoveredTiles;
	}
	
	public Boolean[] getVisibleTiles() {
		return visibleTiles;
	}
	
	public Tile getTile(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return null;
		}
		
		return tiles[width * y + x];
	}
	
	public TileType getTileType(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return null;
		}
		
		return getTile(x, y).getType();
	}
	
	public void setTileType(int x, int y, TileType tile) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return;
		}
		
		tiles[width * y + x].setType(tile);
	}
	
	public Tile[] getAdjacentTiles(int x, int y) {
		Tile[] t = new Tile[Utils.DIRECTIONS.length];
		
		for (int i = 0; i < Utils.DIRECTIONS.length; i++) {
			int[] direction = Utils.DIRECTIONS[i];
			
			t[i] = getTile(x + direction[0], y + direction[1]);
		}
		
		return t;
	}
	
	public TileType[] getAdjacentTileTypes(int x, int y) {
		TileType[] t = new TileType[Utils.DIRECTIONS.length];
		
		for (int i = 0; i < Utils.DIRECTIONS.length; i++) {
			int[] direction = Utils.DIRECTIONS[i];
			
			t[i] = getTileType(x + direction[0], y + direction[1]);
		}
		
		return t;
	}
	
	public List<Tile> getTilesInRadius(int x, int y, int r) {
		List<Tile> found = new ArrayList<>();
		
		for (int j = y - r; j < y + r; j++) {
			for (int i = x - r; i < x + r; i++) {
				if (Utils.distance(x, y, i, j) <= r) {
					Tile t = getTile(i, j);
					
					if (t != null) {
						found.add(t);
					}
				}
			}
		}
		
		return found;
	}
	
	public boolean isTileDiscovered(int x, int y) {
		return !(x < 0 || y < 0 || x >= width || y >= height) && discoveredTiles[y * width + x];
	}
	
	public void discoverTile(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return;
		}
		
		discoveredTiles[width * y + x] = true;
	}
	
	public boolean isTileInvisible(int x, int y) {
		return x < 0 || y < 0 || x >= width || y >= height || !visibleTiles[width * y + x];
	}
	
	public void seeTile(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return;
		}
		
		visibleTiles[y * width + x] = true;
		
		entities.values().stream()
			.filter(e -> e.getX() == x && e.getY() == y)
			.forEach(e -> {
				e.setLastSeenX(x);
				e.setLastSeenY(y);
			});
	}
	
	public void updateSight(Player player) {
		Arrays.fill(visibleTiles, false);
		
		float x = player.getX() + 0.5f;
		float y = player.getY() + 0.5f;
		
		for (int r = 0; r < 360; r++) {
			int corridorVisibility = 0;
			boolean breakNext = false;
			
			for (int i = 0; i < player.getVisibilityRange(); i++) {
				double a = Math.toRadians(r);
				int dx = (int) Math.floor(x + i * Math.cos(a));
				int dy = (int) Math.floor(y + i * Math.sin(a));
				TileType type = getTileType(dx, dy);
				
				if (type == TileType.TILE_CORRIDOR) {
					corridorVisibility += 1;
				}
				
				if (corridorVisibility >= player.getCorridorVisibilityRange()) {
					break;
				}
				
				discoverTile(dx, dy);
				seeTile(dx, dy);
				
				if (dx < 0 || dy < 0 || dx >= width || dy >= height ||
					type.getSolidity() == TileType.Solidity.SOLID ||
					!(dx == player.getX() && dy == player.getY()) && type.isSemiTransparent() ||
					breakNext) {
					break;
				}
				
				if (dx == player.getX() && dy == player.getY() && type.isSemiTransparent()) {
					breakNext = true;
				}
			}
		}
	}
	
	public void seeAll() {
		Arrays.fill(visibleTiles, true);
		Arrays.fill(discoveredTiles, true);
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
		
		Stream.concat(entities.values().stream(), entityAddQueue.stream())
			.filter(e -> e instanceof LightEmitter)
			.forEach(e -> {
				LightEmitter lightEmitter = (LightEmitter) e;
				int index = lightEmitter.getLightIntensity() - 1;
				
				if (index < 0 || index >= LIGHT_MAX_LIGHT_LEVEL) { return; }
				
				Tile tile = new Tile(this, TileType.TILE_DUMMY, e.getX(), e.getY());
				
				if (!isTileInvisible(tile.getX(), tile.getY()) && !isInitial) {
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
			.filter(t -> !isTileInvisible(t.getX(), t.getY()))
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
		
		if (x > 0) { setIntensity(getTile(x - 1, y), intensity, colour, isInitial); }
		if (x < getWidth() - 1) { setIntensity(getTile(x + 1, y), intensity, colour, isInitial); }
		if (y > 0) { setIntensity(getTile(x, y - 1), intensity, colour, isInitial); }
		if (y < getHeight() - 1) { setIntensity(getTile(x, y + 1), intensity, colour, isInitial); }
		
		colour = new Color(
			(int) (colour.getRed() * 0.9f),
			(int) (colour.getGreen() * 0.9f),
			(int) (colour.getBlue() * 0.9f),
			colour.getAlpha()
		);
		
		if (x > 0 && y < getWidth() - 1) { setIntensity(getTile(x - 1, y + 1), intensity, colour, isInitial); }
		if (x < getWidth() - 1 && y > 0) { setIntensity(getTile(x + 1, y - 1), intensity, colour, isInitial); }
		if (x > 0 && y < 0) { setIntensity(getTile(x - 1, y - 1), intensity, colour, isInitial); }
		if (x < getWidth() - 1 && y < getHeight() - 1) {
			setIntensity(getTile(x + 1, y + 1), intensity, colour, isInitial);
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
		if (tile == null || isTileInvisible(tile.getX(), tile.getY()) && !isInitial) {
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
		return null;
	}
}
