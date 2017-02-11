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

public abstract class GeneratorRooms extends DungeonGenerator {
	protected final WeightedCollection<Class<? extends Room>> roomTypes = new WeightedCollection<>();
	
	{
		roomTypes.add(49, RoomBasic.class);
		roomTypes.add(2, RoomWater.class);
		roomTypes.add(1, RoomGraveyard.class);
	}
	
	protected int minRoomWidth = 5;
	protected int maxRoomWidth = 20;
	
	protected int minRoomHeight = 5;
	protected int maxRoomHeight = 9;
	
	protected int minRoomDistanceX = 1;
	protected int maxRoomDistanceX = 15;
	protected int minRoomOffsetX = -4;
	protected int maxRoomOffsetX = 4;
	
	protected int minRoomDistanceY = 1;
	protected int maxRoomDistanceY = 5;
	protected int minRoomOffsetY = -4;
	protected int maxRoomOffsetY = 4;
	
	protected static final float CORRIDOR_LINE_SLOPE = 0.2f;
	
	private static final double PROBABILITY_GOLD_DROP = 0.08;
	
	protected final WeightedCollection<Integer> probabilitySpecialFeatureCount = new WeightedCollection<>();
	
	{
		probabilitySpecialFeatureCount.add(3, 0);
		probabilitySpecialFeatureCount.add(3, 1);
		probabilitySpecialFeatureCount.add(2, 2);
		probabilitySpecialFeatureCount.add(1, 3);
	}
	
	protected final WeightedCollection<Class<? extends SpecialRoomFeature>> probabilitySpecialFeatures
		= new WeightedCollection<>();
	
	{
		probabilitySpecialFeatures.add(15, FeatureFountain.class);
		probabilitySpecialFeatures.add(4, FeatureChest.class);
		probabilitySpecialFeatures.add(1, FeatureAltar.class);
	}
	
	private VerificationPathfinder pathfinder = new VerificationPathfinder();
	
	protected List<Room> rooms = new ArrayList<>();
	
	private Tile startTile;
	private Tile endTile;
	
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

		createRoom(
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
	
	private void createRoom(int roomX, int roomY, int roomWidth, int roomHeight) {
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
					createRoom(newRoomX, newRoomY, newRoomWidth, newRoomHeight);
					
					break;
				}
			}
		}
	}
	
	private void graphRooms() {
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
	
	private void buildCorridors() {
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
						
						buildLine(point.getAx(), point.getAy(), point.getBx(), point.getBy(), tile, true, false);
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
	
	protected void buildLCorridor(ConnectionPoint point) {
		int ax = point.getAx();
		int ay = point.getAy();
		
		int bx = point.getBx();
		int by = point.getBy();
		
		int dx = bx - ax;
		int dy = by - ay;
		
		TileType tile = getCorridorTileType();
		
		if (Math.abs(dx) < 1 || Math.abs(dy) < 1) {
			buildLine(ax, ay, bx, by, tile, true, true);
			
			return;
		}
		
		buildLine(ax, ay, bx, ay, tile, true, true);
		buildLine(bx, ay, bx, by, tile, true, true);
	}
	
	protected void buildSCorridor(ConnectionPoint point) {
		int ax = point.getAx();
		int ay = point.getAy();
		
		int bx = point.getBx();
		int by = point.getBy();
		
		int dx = bx - ax;
		int dy = by - ay;
		
		TileType tile = getCorridorTileType();
		
		if (point.getIntendedOrientation() == Orientation.HORIZONTAL) {
			buildLine(ax, ay, ax + (int) Math.ceil(dx / 2), ay, tile, true, true);
			buildLine(ax + Math.round(dx / 2), ay, ax + (int) Math.floor(dx / 2), by, tile, true, true);
			buildLine(bx, by, ax + (int) Math.floor(dx / 2), by, tile, true, true);
		} else {
			buildLine(ax, ay, ax, ay + (int) Math.ceil(dy / 2), tile, true, true);
			buildLine(ax, ay + Math.round(dy / 2), bx, ay + (int) Math.floor(dy / 2), tile, true, true);
			buildLine(bx, by, bx, ay + (int) Math.floor(dy / 2), tile, true, true);
		}
	}
	
	private void removeStrayRooms() {
		rooms.removeIf(room -> room.getConnectionPoints().size() <= 0);
	}
	
	private void addRoomFeatures() {
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
	
	private void addRandomDrops() {
		rooms.forEach(r -> {
			if (rand.nextDouble() < PROBABILITY_GOLD_DROP) {
				int x = rand.nextInt(r.getWidth() - 2) + r.getX() + 1;
				int y = rand.nextInt(r.getHeight() - 2) + r.getY() + 1;
				
				QuickSpawn.spawnGold(level, x, y, RandomUtils.roll(Math.abs(level.getDepth()) + 2, 6));
			}
		});
	}
	
	private boolean chooseSpawnRoom() {
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
				tsc.setLinkedLevelUUID(Optional.ofNullable(sourceTile.getLevel().getUUID()));
				tsc.setDestinationPosition(sourceTile.getX(), sourceTile.getY());
			}
		}
		
		spawnRoom.setSpawn(true);
		startTile = level.getTileStore().getTile(stairX, stairY);
		level.setSpawnPoint(stairX, stairY);
		
		return true;
	}
	
	private void chooseNextStairsRoom() {
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
