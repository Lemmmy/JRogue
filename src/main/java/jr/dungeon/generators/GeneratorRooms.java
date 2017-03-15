package jr.dungeon.generators;

import jr.ErrorHandler;
import jr.JRogue;
import jr.dungeon.Level;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.generators.rooms.Room;
import jr.dungeon.generators.rooms.RoomBasic;
import jr.dungeon.generators.rooms.RoomGraveyard;
import jr.dungeon.generators.rooms.RoomWater;
import jr.dungeon.generators.rooms.features.FeatureAltar;
import jr.dungeon.generators.rooms.features.FeatureChest;
import jr.dungeon.generators.rooms.features.FeatureFountain;
import jr.dungeon.generators.rooms.features.SpecialRoomFeature;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.states.TileStateClimbable;
import jr.utils.Path;
import jr.utils.RandomUtils;
import jr.utils.Utils;
import jr.utils.WeightedCollection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dungeon generator that generates rooms connected with corridors.
 */
public abstract class GeneratorRooms extends DungeonGenerator {
	/**
	 * {@link WeightedCollection Weighted collection} containing probability that certain room types would spawn.
	 * When overriding, clear the collection first then add your own.
	 */
	protected final WeightedCollection<Class<? extends Room>> roomTypes = new WeightedCollection<>();
	
	{
		roomTypes.add(49, RoomBasic.class);
		roomTypes.add(2, RoomWater.class);
		roomTypes.add(1, RoomGraveyard.class);
	}
	
	/**
	 * {@link WeightedCollection Weighted collection} containing probability that certain doors would spawn.
	 * When overriding, clear the collection first then add your own.
	 */
	protected static final WeightedCollection<TileType> doorTypes = new WeightedCollection<>();
	
	static {
		doorTypes.add(3, TileType.TILE_ROOM_DOOR_LOCKED);
		doorTypes.add(4, TileType.TILE_ROOM_DOOR_CLOSED);
		doorTypes.add(6, TileType.TILE_ROOM_DOOR_OPEN);
	}
	
	/**
	 * Minimum room generation width in tiles.
	 */
	protected int minRoomWidth = 5;
	/**
	 * Maximum room generation width in tiles.
	 */
	protected int maxRoomWidth = 20;
	
	/**
	 * Minimum room generation height in tiles.
	 */
	protected int minRoomHeight = 5;
	/**
	 * Maximum room generation height in tiles.
	 */
	protected int maxRoomHeight = 9;
	
	/**
	 * Minimum distance in tiles on the X axis when placing two rooms.
	 */
	protected int minRoomDistanceX = 1;
	/**
	 * Maximum distance in tiles on the X axis when placing two rooms.
	 */
	protected int maxRoomDistanceX = 15;
	/**
	 * Minimum offset in tiles on the X axis when placing two rooms.
	 */
	protected int minRoomOffsetX = -4;
	/**
	 * Maximum offset in tiles on the X axis when placing two rooms.
	 */
	protected int maxRoomOffsetX = 4;
	
	/**
	 * Minimum distance in tiles on the Y axis when placing two rooms.
	 */
	protected int minRoomDistanceY = 1;
	/**
	 * Maximum distance in tiles on the Y axis when placing two rooms.
	 */
	protected int maxRoomDistanceY = 5;
	/**
	 * Minimum offset in tiles on the Y axis when placing two rooms.
	 */
	protected int minRoomOffsetY = -4;
	/**
	 * Maximum offset in tiles on the Y axis when placing two rooms.
	 */
	protected int maxRoomOffsetY = 4;
	
	/**
	 * Maximum slope (between 0 and 1) at which the coridoors will be L-shaped or S shaped, and instead turn into a
	 * straight line.
	 */
	protected static final float CORRIDOR_LINE_SLOPE = 0.2f;
	
	/**
	 * Probability of a pile of gold spawning in any room.
	 */
	private static final double PROBABILITY_GOLD_DROP = 0.08;
	
	/**
	 * {@link WeightedCollection Weighted probability} of the count of special features in on level.
	 */
	protected final WeightedCollection<Integer> probabilitySpecialFeatureCount = new WeightedCollection<>();
	
	{
		probabilitySpecialFeatureCount.add(3, 0);
		probabilitySpecialFeatureCount.add(3, 1);
		probabilitySpecialFeatureCount.add(2, 2);
		probabilitySpecialFeatureCount.add(1, 3);
	}
	
	/**
	 * {@link WeightedCollection Weighted probablity} of special features in a level.
	 *
	 * During generation, a random count will be chosen based on a weighted probability (see
	 * {@link #probabilitySpecialFeatureCount}), and then for each of the count, a random feature is chosen from this
	 * list.
	 */
	protected final WeightedCollection<Class<? extends SpecialRoomFeature>> probabilitySpecialFeatures
		= new WeightedCollection<>();
	
	{
		probabilitySpecialFeatures.add(15, FeatureFountain.class);
		probabilitySpecialFeatures.add(4, FeatureChest.class);
		probabilitySpecialFeatures.add(1, FeatureAltar.class);
	}
	
	/**
	 * The {@link VerificationPathfinder Pathfinder} used to verify a level is complete - the pathfinder travels from
	 * the Level's start point to the end point during the verification step, and if it can't reach, the level is
	 * scrapped and a new one is generated.
	 */
	private VerificationPathfinder pathfinder = new VerificationPathfinder();
	
	/**
	 * List of rooms created during generation.
	 */
	protected List<Room> rooms = new ArrayList<>();
	
	/**
	 * The tile that the player enters this map from - the level entrance e.g. the staircase up.
	 */
	private Tile startTile;
	/**
	 * The primary destination tile for this level - does not include branches. Typically the stiarcase down.
	 */
	private Tile endTile;
	
	/**
	 * @param level The level that this generator is generating for.
	 * @param sourceTile The tile that the player came from in the pervious level.
	 */
	public GeneratorRooms(Level level, Tile sourceTile) {
		super(level, sourceTile);
	}
	
	public abstract Class<? extends DungeonGenerator> getNextGenerator();
	
	public TileType getDownstairsTileType() {
		return TileType.TILE_ROOM_STAIRS_DOWN;
	}
	
	public TileType getUpstairsTileType() {
		return TileType.TILE_ROOM_STAIRS_UP;
	}
	
	public TileType getGroundTileType() {
		return TileType.TILE_GROUND;
	}
	
	@Override
	public boolean generate() {
		if (getGroundTileType() != TileType.TILE_GROUND) {
			for (int y = 0; y < level.getHeight(); ++y) {
				for (int x = 0; x < level.getWidth(); ++x) {
					level.getTileStore().setTileType(x, y, getGroundTileType());
				}
			}
		}
		
		int width = nextInt(minRoomWidth, maxRoomWidth);
		int height = nextInt(minRoomHeight, maxRoomHeight);

		createRooms(
			nextInt(1, level.getWidth() - width - 1),
			nextInt(1, level.getHeight() - height - 1),
			width,
			height
		);
		
		Collections.shuffle(rooms);
		
		graphRooms();
		buildCorridors();
		removeStrayRooms();
		if (!chooseSpawnRoom()) { return false; }
		chooseNextStairsRoom();
		addRoomFeatures();
		addRandomDrops();
		
		return true;
	}
	
	/**
	 * Recursive method that fills the map with rooms.
	 *
	 * @param roomX The X coordinate of the top left corner of the first room.
	 * @param roomY The Y coordinate of the top right corner of the first room.
	 * @param roomWidth The width of the first room.
	 * @param roomHeight The height of the first room.
	 */
	protected void createRooms(int roomX, int roomY, int roomWidth, int roomHeight) {
		buildRoom(roomTypes.next(), roomX, roomY, roomWidth, roomHeight);
		
		for (int[] direction : Utils.DIRECTIONS) {
			for (int attempts = 1; attempts < 5; ++attempts) {
				int newRoomWidth = nextInt(minRoomWidth, maxRoomWidth);
				int newRoomHeight = nextInt(minRoomHeight, maxRoomHeight);
				
				int newRoomX = roomX + direction[0] * roomWidth +
					direction[0] * nextInt(minRoomDistanceX, maxRoomDistanceX) +
					direction[1] * nextInt(minRoomOffsetX, maxRoomOffsetX);
				int newRoomY = roomY + direction[1] * roomHeight +
					direction[1] * nextInt(minRoomDistanceY, maxRoomDistanceY) +
					direction[0] * nextInt(minRoomOffsetY, maxRoomOffsetY);
				
				if (canBuildRoom(newRoomX, newRoomY, newRoomWidth, newRoomHeight)) {
					createRooms(newRoomX, newRoomY, newRoomWidth, newRoomHeight);
					
					break;
				}
			}
		}
	}
	
	/**
	 * Put the rooms into a linked graph, to calculate the Minimum Spanning tree (MST) later.
	 */
	protected void graphRooms() {
		for (Room a : rooms) {
			for (Room b : rooms) {
				boolean skip = false;
				
				double abDist = Math.pow(a.getCenterX() - b.getCenterX(), 2) +
					Math.pow(a.getCenterY() - b.getCenterY(), 2);
				
				for (Room c : rooms) {
					if (c.equals(a) || c.equals(b)) {
						continue;
					}
					
					double acDist = Math.pow(a.getCenterX() - c.getCenterX(), 2) +
						Math.pow(a.getCenterY() - c.getCenterY(), 2);
					double bcDist = Math.pow(b.getCenterX() - c.getCenterX(), 2) +
						Math.pow(b.getCenterY() - c.getCenterY(), 2);
					
					if (acDist < abDist && bcDist < abDist) {
						skip = true;
					}
					
					if (skip) {
						break;
					}
				}
				
				if (!skip) {
					a.addTouching(b);
				}
			}
		}
	}
	
	/**
	 * Places a random door and fills the surrounding ground with corridors to ensure it's reachable.
	 * Door is chosen by getting a random value from the weighted #doorTypes collection.
	 *
	 * @param x The X coordinate to place the door.
	 * @param y The Y coordinate to place the door.
	 */
	protected void safePlaceDoor(int x, int y) {
		level.getTileStore().setTileType(x, y, doorTypes.next());
		
		for (int[] direction : Utils.DIRECTIONS) {
			int nx = x + direction[0];
			int ny = y + direction[1];
			
			TileType t = level.getTileStore().getTileType(nx, ny);
			
			if (t == TileType.TILE_GROUND) {
				level.getTileStore().setTileType(nx, ny, TileType.TILE_CORRIDOR);
			}
		}
	}
	
	/**
	 * Places a line of tiles, placing a door if it intersects a wall.
	 *
	 * @see DungeonGenerator#buildLine(int, int, int, int, TileType)
	 *
	 * @param startX The starting X position of the line.
	 * @param startY The starting Y position of the line.
	 * @param endX The ending X position of the line.
	 * @param endY The ending Y position of the line.
	 * @param tile The tile to build the line with.
	 */
	protected void buildLineWithDoors(int startX,
							 int startY,
							 int endX,
							 int endY,
							 TileType tile) {
		float diffX = endX - startX;
		float diffY = endY - startY;
		
		float dist = Math.abs(diffX) + Math.abs(diffY);
		
		float dx = diffX / dist;
		float dy = diffY / dist;
		
		for (int i = 0; i <= Math.ceil(dist); i++) {
			int x = Math.round(startX + dx * i);
			int y = Math.round(startY + dy * i);
			
			if (level.getTileStore().getTileType(x, y).isBuildable()) {
				level.getTileStore().setTileType(x, y, tile);
			} else if (canPlaceDoor(x, y)) {
				safePlaceDoor(x, y);
			}
		}
	}
	
	/**
	 * Builds the corridors to connect all the rooms. Uses different corridor methods based on the slope of the room.
	 *
	 * @see #buildLCorridor(ConnectionPoint)
	 * @see #buildSCorridor(ConnectionPoint)
	 */
	protected void buildCorridors() {
		for (Room a : rooms) {
			for (Room b : a.getTouching()) {
				float dx = b.getCenterX() - a.getCenterX();
				float dy = b.getCenterY() - a.getCenterY();
				
				if (dx > 0) {
					float slope = Math.abs(dy / dx);
					
					if (slope > 0.5f) {
						slope = Math.abs(-1f / slope);
					}
					
					ConnectionPoint point = getConnectionPoint(a, b);
					
					a.addConnectionPoint(point);
					b.addConnectionPoint(point);
					
					if (slope <= CORRIDOR_LINE_SLOPE) {
						TileType tile = getCorridorTileType();
						
						buildLineWithDoors(point.getAx(), point.getAy(), point.getBx(), point.getBy(), tile);
					} else {
						if (point.getOrientationA() == point.getOrientationB()) {
							buildSCorridor(point);
						} else {
							buildLCorridor(point);
						}
					}
					
					safePlaceDoor(point.getAx(), point.getAy());
					safePlaceDoor(point.getBx(), point.getBy());
				}
			}
		}
	}
	
	/**
	 * Builds an L-shaped corridoor between two points.
	 *
	 * @param point The {@link jr.dungeon.generators.DungeonGenerator.ConnectionPoint connection point} containing
	 *                 the room connection data.
	 */
	protected void buildLCorridor(ConnectionPoint point) {
		int ax = point.getAx();
		int ay = point.getAy();
		
		int bx = point.getBx();
		int by = point.getBy();
		
		int dx = bx - ax;
		int dy = by - ay;
		
		TileType tile = getCorridorTileType();
		
		if (Math.abs(dx) < 1 || Math.abs(dy) < 1) {
			buildLineWithDoors(ax, ay, bx, by, tile);
			
			return;
		}
		
		buildLineWithDoors(ax, ay, bx, ay, tile);
		buildLineWithDoors(bx, ay, bx, by, tile);
	}
	
	/**
	 * Builds an S-shaped corridor between two points.
	 *
	 * @param point The {@link jr.dungeon.generators.DungeonGenerator.ConnectionPoint connection point} containing
	 *                 the room connection data.
	 */
	protected void buildSCorridor(ConnectionPoint point) {
		int ax = point.getAx();
		int ay = point.getAy();
		
		int bx = point.getBx();
		int by = point.getBy();
		
		int dx = bx - ax;
		int dy = by - ay;
		
		TileType tile = getCorridorTileType();
		
		if (point.getIntendedOrientation() == Orientation.HORIZONTAL) {
			buildLineWithDoors(ax, ay, ax + (int) Math.ceil(dx / 2), ay, tile);
			buildLineWithDoors(ax + Math.round(dx / 2), ay, ax + (int) Math.floor(dx / 2), by, tile);
			buildLineWithDoors(bx, by, ax + (int) Math.floor(dx / 2), by, tile);
		} else {
			buildLineWithDoors(ax, ay, ax, ay + (int) Math.ceil(dy / 2), tile);
			buildLineWithDoors(ax, ay + Math.round(dy / 2), bx, ay + (int) Math.floor(dy / 2), tile);
			buildLineWithDoors(bx, by, bx, ay + (int) Math.floor(dy / 2), tile);
		}
	}
	
	/**
	 * Removes rooms with no connection points after graphing.
	 */
	protected void removeStrayRooms() {
		rooms.removeIf(room -> room.getConnectionPoints().size() <= 0);
		
		// TODO: do we actually remove the rooms tiles??
	}
	
	/**
	 * Adds room-specific features and special dungeon features to selected rooms.
	 *
	 * @see SpecialRoomFeature
	 */
	protected void addRoomFeatures() {
		rooms.forEach(Room::addFeatures);
		
		int featureCount = probabilitySpecialFeatureCount.next();
		
		for (int i = 0; i < featureCount; i++) {
			try {
				Class<? extends SpecialRoomFeature> featureClass = probabilitySpecialFeatures.next();
				Constructor featureConstructor = featureClass.getConstructor();
				SpecialRoomFeature feature = (SpecialRoomFeature) featureConstructor.newInstance();
				feature.generate(RandomUtils.randomFrom(rooms));
			} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
				ErrorHandler.error("Error adding room features", e);
			}
		}
	}
	
	/**
	 * Add random item drops to some of the rooms (e.g. gold).
	 */
	protected void addRandomDrops() {
		rooms.forEach(r -> {
			if (rand.nextDouble() < PROBABILITY_GOLD_DROP) {
				int x = rand.nextInt(r.getWidth() - 2) + r.getX() + 1;
				int y = rand.nextInt(r.getHeight() - 2) + r.getY() + 1;
				
				QuickSpawn.spawnGold(level, x, y, RandomUtils.roll(Math.abs(level.getDepth()) + 2, 6));
			}
		});
	}
	
	/**
	 * Chooses a room for the player to spawn in, and sets the spawn tile.
	 *
	 * @return Whether or not a valid spawn room was found.
	 */
	protected boolean chooseSpawnRoom() {
		List<Room> temp = new ArrayList<>(rooms);
		temp.sort(Comparator.comparingInt(a -> a.getConnectionPoints().size()));
		
		List<Room> temp2 = temp.stream()
			.filter(room -> room.getConnectionPoints().size() == temp.get(temp.size() - 1)
				.getConnectionPoints().size())
			.collect(Collectors.toList());
		
		if (temp2.isEmpty()) {
			return false;
		}
		
		Room spawnRoom = RandomUtils.randomFrom(temp2);
		
		if (spawnRoom == null) {
			return false;
		}
		
		int stairX = nextInt(spawnRoom.getX() + 2, spawnRoom.getX() + spawnRoom.getWidth() - 2);
		int stairY = nextInt(spawnRoom.getY() + 2, spawnRoom.getY() + spawnRoom.getHeight() - 2);
		
		if (sourceTile != null) {
			Tile spawnTile = level.getTileStore().getTile(stairX, stairY);
			spawnTile.setType(getUpstairsTileType());
			
			if (sourceTile.getLevel().getDepth() < level.getDepth()) {
				spawnTile.setType(getDownstairsTileType());
			}
			
			if (spawnTile.getState() instanceof TileStateClimbable) {
				TileStateClimbable tsc = (TileStateClimbable) spawnTile.getState();
				tsc.setLinkedLevelUUID(sourceTile.getLevel().getUUID());
				tsc.setDestinationPosition(sourceTile.getX(), sourceTile.getY());
			}
		}
		
		spawnRoom.setSpawn(true);
		startTile = level.getTileStore().getTile(stairX, stairY);
		level.setSpawnPoint(stairX, stairY);
		
		return true;
	}
	
	/**
	 * Chooses a room to contain the primary level exit (e.g. the stairs down).
	 */
	protected void chooseNextStairsRoom() {
		Optional<Room> spawnRoom = rooms.stream()
			.filter(Room::isSpawn)
			.findFirst();
		
		List<Room> temp = rooms.stream()
			.filter(room -> !room.isSpawn())
			.collect(Collectors.toList());
		
		spawnRoom.ifPresent(room -> temp.sort(Comparator.comparingDouble(a -> Utils.distanceSq(
			a.getCenterX(),
			a.getCenterY(),
			spawnRoom.get().getCenterX(),
			spawnRoom.get().getCenterX()
			))
		));
		
		List<Room> possibleRooms = temp.stream()
			.skip(temp.size() - 5)
			.collect(Collectors.toList());
		
		Room nextStairsRoom = RandomUtils.randomFrom(possibleRooms);
		assert nextStairsRoom != null;
		
		int stairX = nextInt(
			nextStairsRoom.getX() + 2,
			nextStairsRoom.getX() + nextStairsRoom.getWidth() - 2
		);
		int stairY = nextInt(
			nextStairsRoom.getY() + 2,
			nextStairsRoom.getY() + nextStairsRoom.getHeight() - 2
		);
		
		level.getTileStore().setTileType(stairX, stairY, getDownstairsTileType());
		
		if (sourceTile != null && sourceTile.getLevel().getDepth() < level.getDepth()) {
			level.getTileStore().setTileType(stairX, stairY, getUpstairsTileType());
		}
		
		Tile stairTile = level.getTileStore().getTile(stairX, stairY);
		
		if (stairTile.getState() instanceof TileStateClimbable) {
			TileStateClimbable tsc = (TileStateClimbable) stairTile.getState();
			tsc.setDestinationGenerator(getNextGenerator());
		}
		
		endTile = level.getTileStore().getTile(stairX, stairY);
	}
	
	/**
	 * Use the #pathfinder to verify whether the level is complete - whether or not its possible to pathfind from the
	 * level's entrance to its exit.
	 *
	 * @return Whether or not the level is fully reachable.
	 */
	protected boolean verify() {
		Path path = pathfinder.findPath(
			level,
			startTile.getX(),
			startTile.getY(),
			endTile.getX(),
			endTile.getY(),
			Integer.MAX_VALUE,
			true,
			new ArrayList<>()
		);
		
		if (path == null) {
			JRogue.getLogger().debug("Level was generated unreachable - regenerating");
		}
		
		return path != null;
	}
	
	/**
	 * @param roomX The X position of the room's top-left corner.
	 * @param roomY The Y position of the room's top-right corner.
	 * @param roomWidth The width of the room.
	 * @param roomHeight The height of the room.
	 *
	 * @return Whether or not it's possible to build a room at the specified location.
	 */
	protected boolean canBuildRoom(int roomX, int roomY, int roomWidth, int roomHeight) {
		// the offsets are to prevent rooms directly touching each other
		
		for (int y = roomY - 2; y < roomY + roomHeight + 2; y++) {
			for (int x = roomX - 2; x < roomX + roomWidth + 2; x++) {
				if (level.getTileStore().getTileType(x, y) == null || !level.getTileStore().getTileType(x, y).isBuildable()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	protected Room buildRoom(Class<? extends Room> roomType, int roomX, int roomY, int roomWidth, int roomHeight) {
		try {
			Constructor<? extends Room> roomConstructor = roomType.getConstructor(
				Level.class, int.class, int.class, int.class, int.class
			);
			
			Room room = roomConstructor.newInstance(level, roomX, roomY, roomWidth, roomHeight);
			room.build(this);
			
			rooms.add(room);
			return room;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			JRogue.getLogger().error("Error building rooms", e);
		}
		
		return null;
	}
	
	public TileType getWallTileType() {
		return TileType.TILE_ROOM_WALL;
	}
	
	public TileType getFloorTileType() {
		return TileType.TILE_ROOM_FLOOR;
	}
	
	public TileType getTorchTileType() {
		return TileType.TILE_ROOM_TORCH_FIRE;
	}
	
	public TileType getCorridorTileType() {
		return TileType.TILE_CORRIDOR;
	}
}
