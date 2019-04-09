package jr.dungeon.generators;

import com.google.gson.annotations.Expose;
import jr.ErrorHandler;
import jr.JRogue;
import jr.dungeon.Level;
import jr.dungeon.TileStore;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.generators.rooms.Room;
import jr.dungeon.generators.rooms.RoomBasic;
import jr.dungeon.generators.rooms.RoomGraveyard;
import jr.dungeon.generators.rooms.RoomWater;
import jr.dungeon.generators.rooms.features.FeatureAltar;
import jr.dungeon.generators.rooms.features.FeatureChest;
import jr.dungeon.generators.rooms.features.FeatureFountain;
import jr.dungeon.generators.rooms.features.SpecialRoomFeature;
import jr.dungeon.serialisation.DungeonRegistries;
import jr.dungeon.serialisation.DungeonRegistry;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.states.TileStateClimbable;
import jr.utils.*;
import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.round;
import static jr.dungeon.generators.BuildingUtils.buildLine;
import static jr.dungeon.generators.BuildingUtils.fillArea;
import static jr.utils.QuickMaths.iceil;
import static jr.utils.QuickMaths.ifloor;

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
     * Maximum slope (between 0 and 1) at which the corridors will be L-shaped or S shaped, and instead turn into a
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
     * The primary destination tile for this level - does not include branches. Typically the staircase down.
     */
    private Tile endTile;
    
    @Expose @Getter private Map<String, Integer> roomFeatures = new HashMap<>();
    
    /**
     * @param level The level that this generator is generating for.
     * @param sourceTile The tile that the player came from in the previous level.
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
            fillArea(tileStore, Point.ZERO, levelWidth, levelHeight, getGroundTileType());
        }
        
        int width = nextInt(minRoomWidth, maxRoomWidth);
        int height = nextInt(minRoomHeight, maxRoomHeight);

        createRooms(
            Point.get(nextInt(1, levelWidth - width - 1), nextInt(1, levelHeight - height - 1)),
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
     * @param point The position of the top left corner of the first room.
     * @param roomWidth The width of the first room.
     * @param roomHeight The height of the first room.
     */
    private static int roomCalls = 0;
    protected void createRooms(Point position, int roomWidth, int roomHeight) {
        if (roomCalls++ >= 5000) System.exit(1);
        
        buildRoom(roomTypes.next(), position, roomWidth, roomHeight);
        
        for (VectorInt direction : Directions.CARDINAL) {
            for (int attempts = 1; attempts < 5; ++attempts) {
                int newRoomWidth = nextInt(minRoomWidth, maxRoomWidth);
                int newRoomHeight = nextInt(minRoomHeight, maxRoomHeight);
                
                Point newPosition = Point.get(
                    position.x + direction.x * roomWidth +
                    direction.x * nextInt(minRoomDistanceX, maxRoomDistanceX) +
                    direction.y * nextInt(minRoomOffsetX, maxRoomOffsetX),
                    position.y + direction.y * roomHeight +
                    direction.y * nextInt(minRoomDistanceY, maxRoomDistanceY) +
                    direction.x * nextInt(minRoomOffsetY, maxRoomOffsetY)
                );
                
                if (canBuildRoom(newPosition, newRoomWidth, newRoomHeight)) {
                    createRooms(newPosition, newRoomWidth, newRoomHeight);
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
                
                double abDist = Distance.sqf(a.getCenter(), b.getCenter());
                
                for (Room c : rooms) {
                    if (c.equals(a) || c.equals(b)) continue;
                    
                    double acDist = Distance.sqf(a.getCenter(), c.getCenter());
                    double bcDist = Distance.sqf(b.getCenter(), c.getCenter());
                    
                    if (acDist < abDist && bcDist < abDist) {
                        skip = true;
                    }
                    
                    if (skip) {
                        break;
                    }
                }
                
                if (!skip) {
                    a.addConnected(b);
                }
            }
        }
    }
    
    /**
     * Places a random door and fills the surrounding ground with corridors to ensure it's reachable.
     * Door is chosen by getting a random value from the weighted #doorTypes collection.
     *
     * @param point The position to place the door.
     */
    protected void safePlaceDoor(Point point) {
        tileStore.setTileType(point, doorTypes.next());
        
        Directions.cardinal()
            .map(point::add)
            .filter(p -> tileStore.getTileType(p) == TileType.TILE_GROUND)
            .forEach(p -> tileStore.setTileType(p, TileType.TILE_CORRIDOR));
    }
    
    /**
     * Places a line of tiles, placing a door if it intersects a wall.
     *
     * @see BuildingUtils#buildLine(TileStore, Point, Point, BuildingUtils.TileBuilder)
     *
     * @param start The starting position of the line.
     * @param end The ending position of the line.
     * @param tile The tile to build the line with.
     */
    protected void buildLineWithDoors(Point start, Point end, TileType tile) {
        buildLine(tileStore, start, end, (t, p) -> {
            if (tileStore.getTileType(p).isBuildable()) {
                return tile;
            } else if (canPlaceDoor(p)) {
                safePlaceDoor(p);
            }
            
            return null;
        });
    }
    
    /**
     * Builds the corridors to connect all the rooms. Uses different corridor methods based on the slope of the room.
     *
     * @see #buildLCorridor(ConnectionPoint)
     * @see #buildSCorridor(ConnectionPoint)
     */
    protected void buildCorridors() {
        for (Room a : rooms) {
            for (Room b : a.getConnectedRooms()) {
                VectorInt d = VectorInt.between(a.getCenter(), b.getCenter());
                
                if (d.x > 0) {
                    float slope = abs(d.y / d.x);
                    if (slope > 0.5f) slope = abs(-1f / slope);
                    
                    ConnectionPoint point = getConnectionPoint(a, b);
                    
                    a.addConnectionPoint(point);
                    b.addConnectionPoint(point);
                    
                    if (slope <= CORRIDOR_LINE_SLOPE) {
                        TileType tile = getCorridorTileType();
                        
                        buildLineWithDoors(point.a, point.b, tile);
                    } else {
                        if (point.getOrientationA() == point.getOrientationB()) {
                            buildSCorridor(point);
                        } else {
                            buildLCorridor(point);
                        }
                    }
                    
                    safePlaceDoor(point.a);
                    safePlaceDoor(point.b);
                }
            }
        }
    }
    
    /**
     * Builds an L-shaped corridor between two points.
     *
     * @param point The {@link ConnectionPoint connection point} containing
     *                 the room connection data.
     */
    protected void buildLCorridor(ConnectionPoint point) {
        Point a = point.a; Point b = point.b;
        VectorInt d = VectorInt.between(a, b);
        TileType tile = getCorridorTileType();
        
        if (abs(d.x) < 1 || abs(d.y) < 1) {
            buildLineWithDoors(a, b, tile);
            
            return;
        }
        
        buildLineWithDoors(a, Point.get(b.x, a.y), tile);
        buildLineWithDoors(Point.get(b.x, a.y), b, tile);
    }
    
    /**
     * Builds an S-shaped corridor between two points.
     *
     * @param point The {@link ConnectionPoint connection point} containing
     *                 the room connection data.
     */
    protected void buildSCorridor(ConnectionPoint point) {
        Point a = point.a; Point b = point.b;
        VectorInt d = VectorInt.between(a, b);
        TileType tile = getCorridorTileType();
        
        if (point.getIntendedOrientation() == Orientation.HORIZONTAL) {
            buildLineWithDoors(a, Point.get(a.x + iceil(d.x / 2), a.y), tile);
            buildLineWithDoors(Point.get(a.x + round(d.x / 2), a.y), Point.get(a.x + ifloor(d.x / 2), b.y), tile);
            buildLineWithDoors(b, Point.get(a.x + ifloor(d.x / 2), b.y), tile);
        } else {
            buildLineWithDoors(a, Point.get(a.x, a.y + iceil(d.y / 2)), tile);
            buildLineWithDoors(Point.get(a.x, a.y + round(d.y / 2)), Point.get(b.x, a.y + ifloor(d.y / 2)), tile);
            buildLineWithDoors(b, Point.get(b.x, a.y + ifloor(d.y / 2)), tile);
        }
    }
    
    /**
     * Removes rooms with no connection points after graphing.
     */
    protected void removeStrayRooms() {
        rooms.removeIf(room -> room.getConnectionPoints().size() <= 0);
        
        // TODO: do we actually remove the rooms tiles??
    }
    
    public static DungeonRegistry<SpecialRoomFeature> getRoomFeatureRegistry() {
        return DungeonRegistries.findRegistryForClass(SpecialRoomFeature.class)
            .orElseThrow(() -> new RuntimeException("Couldn't find SpecialRoomFeature registry in GeneratorRooms"));
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
                String featureID = getRoomFeatureRegistry().getID(featureClass)
                    .orElseThrow(() -> new RuntimeException(String.format("Couldn't find ID for SpecialRoomFeature `%s` in GeneratorRooms", featureClass.getName())));
                
                if (roomFeatures.containsKey(featureID)) {
                    roomFeatures.put(featureID, roomFeatures.get(featureID) + 1);
                } else {
                    roomFeatures.put(featureID, 1);
                }
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
            if (RAND.nextDouble() < PROBABILITY_GOLD_DROP) {
                QuickSpawn.spawnGold(level, r.randomPoint(), RandomUtils.roll(abs(level.getDepth()) + 2, 6));
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
        if (temp2.isEmpty()) return false;
        
        Room spawnRoom = RandomUtils.randomFrom(temp2);
        if (spawnRoom == null) return false;
        
        Point stairPoint = spawnRoom.randomPoint();
        
        if (sourceTile != null) {
            Tile spawnTile = tileStore.getTile(stairPoint);
            spawnTile.setType(getUpstairsTileType());
            
            if (sourceTile.getLevel().getDepth() < level.getDepth()) {
                spawnTile.setType(getDownstairsTileType());
            }
            
            if (spawnTile.getState() instanceof TileStateClimbable) {
                TileStateClimbable tsc = (TileStateClimbable) spawnTile.getState();
                tsc.setLinkedLevelUUID(sourceTile.getLevel().getUUID());
                tsc.setDestinationPosition(sourceTile.position);
            }
        }
        
        spawnRoom.setSpawn(true);
        startTile = tileStore.getTile(stairPoint);
        level.setSpawnPoint(stairPoint);
        
        return true;
    }
    
    /**
     * Chooses a room to contain the primary level exit (e.g. the stairs down).
     */
    protected void chooseNextStairsRoom() {
        Room spawnRoom = rooms.stream()
            .filter(Room::isSpawn)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Spawn room is missing?!"));
        
        List<Room> possibleRooms = rooms.stream()
            .filter(room -> !room.isSpawn())
            .sorted(Comparator.<Room>comparingDouble(a -> Distance.sqf(a.getCenter(), spawnRoom.getCenter())).reversed())
            .limit(5)
            .collect(Collectors.toList());
        
        Room nextStairsRoom = RandomUtils.randomFrom(possibleRooms);
        assert nextStairsRoom != null;
        Point stairPoint = nextStairsRoom.randomPoint();
        
        tileStore.setTileType(stairPoint, getDownstairsTileType());
        
        if (sourceTile != null && sourceTile.getLevel().getDepth() < level.getDepth()) {
            tileStore.setTileType(stairPoint, getUpstairsTileType());
        }
        
        Tile stairTile = tileStore.getTile(stairPoint);
        
        if (stairTile.getState() instanceof TileStateClimbable) {
            TileStateClimbable tsc = (TileStateClimbable) stairTile.getState();
            tsc.setDestinationGenerator(getNextGenerator());
        }
        
        endTile = tileStore.getTile(stairPoint);
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
            startTile.position,
            endTile.position,
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
     * @param p The position of the room's top-left corner.
     * @param roomWidth The width of the room.
     * @param roomHeight The height of the room.
     *
     * @return Whether or not it's possible to build a room at the specified location.
     */
    protected boolean canBuildRoom(Point p, int roomWidth, int roomHeight) {
        // the offsets are to prevent rooms directly touching each other
        
        for (int y = p.y - 2; y <= p.y + roomHeight + 2; y++) {
            for (int x = p.x - 2; x <= p.x + roomWidth + 2; x++) {
                TileType t = tileStore.getTileType(Point.get(x, y));
                
                if (t == null || !t.isBuildable()) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Builds a room from a room class with the specified dimensions and adds it to the #rooms list.
     *
     * @param roomType The room class to instantiate and build.
     * @param position The position of the top left corner of the room.
     * @param roomWidth The width of the room.
     * @param roomHeight The height of the room.
     *
     * @return The newly built room.
     */
    protected Room buildRoom(Class<? extends Room> roomType, Point position, int roomWidth, int roomHeight) {
        try {
            Constructor<? extends Room> roomConstructor = roomType.getConstructor(
                Level.class, Point.class, int.class, int.class
            );
            
            Room room = roomConstructor.newInstance(level, position, roomWidth, roomHeight);
            room.build(this);
            
            rooms.add(room);
            return room;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            ErrorHandler.error("Error building rooms", e);
        }
        
        return null;
    }
    
    /**
     * @param point The position to check.
     *
     * @return Whether or not its possible to place a door here - checks if the tile is a wall, all adjacent tiles are
     * not doors, and this tile is not a wall corner.
     */
    public boolean canPlaceDoor(Point point) {
        if (tileStore.getTileType(point).isWall()) {
            TileType[] adjacentTiles = tileStore.getAdjacentTileTypes(point);
            
            for (TileType tile : adjacentTiles) {
                if (tile == TileType.TILE_ROOM_DOOR_CLOSED) {
                    return false;
                }
            }
            
            return getWallOrientation(adjacentTiles) != Orientation.CORNER;
        }
        
        return false;
    }
    
    /**
     * @param adjacentTiles The list of adjacent tile types from
     * {@link jr.dungeon.TileStore#getAdjacentTileTypes(Point)}.
     *
     * @return The {@link Orientation} of the wall.
     *
     * @see Orientation
     */
    protected Orientation getWallOrientation(TileType[] adjacentTiles) {
        boolean h = adjacentTiles[0].isWall() || adjacentTiles[1].isWall();
        boolean v = adjacentTiles[2].isWall() || adjacentTiles[3].isWall();
        
        if (h && !v) {
            return Orientation.HORIZONTAL;
        } else if (!h && v) {
            return Orientation.VERTICAL;
        } else {
            return Orientation.CORNER;
        }
    }
    
    /**
     * @param point The position to check.
     *
     * @return The {@link Orientation} of the wall.
     */
    protected Orientation getWallOrientation(Point point) {
        return getWallOrientation(tileStore.getAdjacentTileTypes(point));
    }
    
    /**
     * Gets a {@link ConnectionPoint} between two rooms - this finds a location that a door can be placed on each room,
     * and a strategy for corridors to be built connecting them.
     *
     * @param a One of the two rooms to be connected.
     * @param b The other of the two rooms to be connected.
     *
     * @return The {@link ConnectionPoint} between the two rooms.
     */
    protected ConnectionPoint getConnectionPoint(Room a, Room b) {
        VectorInt d = VectorInt.between(a.getCenter(), b.getCenter()).abs();
        
        /* bottom  left */     Point abl = a.position;     Point bbl = b.position;
        /*    center    */    Point ac  = a.getCenter();     Point bc  = b.getCenter();
        /*  top  right     */    Point atr = a.position.add(a.width, a.height);
                            Point btr = b.position.add(b.width, b.height);
        
        if (d.x > d.y) {
            if (
                d.x <= 5 ||
                bc.x < ac.x ||
                atr.x >= bbl.x ||
                btr.x <= abl.x
            ) {
                if (btr.x > atr.x) {
                    return new ConnectionPoint(
                        Point.get(atr.x - 1, ac.y),
                        Point.get(bc.x, btr.y - 1),
                        Orientation.HORIZONTAL
                    );
                } else {
                    return new ConnectionPoint(
                        Point.get(btr.x - 1, bc.y),
                        Point.get(ac.x, atr.y - 1),
                        Orientation.HORIZONTAL
                    );
                }
            } else {
                if (bbl.x > abl.x || btr.x > atr.x) {
                    return new ConnectionPoint(
                        Point.get(atr.x - 1, ac.y),
                        Point.get(bbl.x, bc.y),
                        Orientation.HORIZONTAL
                    );
                } else {
                    return new ConnectionPoint(
                        Point.get(btr.x - 1, bc.y),
                        Point.get(abl.x, ac.y),
                        Orientation.HORIZONTAL
                    );
                }
            }
        } else {
            if (
                d.y <= 5 ||
                bc.x - ac.x < 0 ||
                atr.y == bbl.y ||
                btr.y == abl.y
            ) {
                if (btr.y > atr.y) {
                    return new ConnectionPoint(
                        Point.get(ac.x, atr.y - 1),
                        Point.get(btr.x - 1, bc.y),
                        Orientation.VERTICAL
                    );
                } else {
                    return new ConnectionPoint(
                        Point.get(bc.x, btr.y - 1),
                        Point.get(atr.x - 1, ac.y),
                        Orientation.VERTICAL
                    );
                }
            } else {
                if (btr.y > atr.y) {
                    return new ConnectionPoint(
                        Point.get(ac.x, atr.y - 1),
                        Point.get(bc.x, bbl.y),
                        Orientation.VERTICAL
                    );
                } else {
                    return new ConnectionPoint(
                        Point.get(bc.x, btr.y - 1),
                        Point.get(ac.x, abl.y),
                        Orientation.VERTICAL
                    );
                }
            }
        }
    }
    
    /**
     * The orientation of a wall, based on its surrounding tiles.
     */
    public enum Orientation {
        /**
         * The wall is horizontal - it has adjacent walls west amd east.
         */
        HORIZONTAL,
        /**
         * The wall is vertical - it has adjacent walls north and south.
         */
        VERTICAL,
        /**
         * The wall is a corner - it has one adjacent wall on its horizontal and vertical axis.
         */
        CORNER
    }
    
    /**
     * A connection point between two rooms - decides where doors should be built, and how a corridor should be built
     * between them.
     */
    @Getter
    public class ConnectionPoint {
        /**
         * The position of the first room's door.
         */
        protected final Point a;
        /**
         * The position of the second room's door.
         */
        protected final Point b;
        
        /**
         * The intended orientation of the corridor to be built.
         */
        private Orientation intendedOrientation;
        /**
         * The orientation of the wall where door A will be built.
         */
        private Orientation orientationA;
        /**
         * The orientation of the wall where door B will be built.
         */
        private Orientation orientationB;
        
        /**
         * @param a The position of the first room's door.
         * @param b The position of the second room's door.
         * @param intendedOrientation The intended orientation of the corridor to be built.
         */
        public ConnectionPoint(Point a, Point b, Orientation intendedOrientation) {
            this.a = a;
            this.b = b;
            
            this.intendedOrientation = intendedOrientation;
            this.orientationA = getWallOrientation(a);
            this.orientationB = getWallOrientation(b);
        }
    }
    
    public TileType getWallTileType() {
        return TileType.TILE_ROOM_WALL;
    }
    
    public TileType getFloorTileType() {
        return TileType.TILE_ROOM_FLOOR;
    }
    
    public TileType getTorchTileType() {
        return TileType.TILE_ROOM_TORCH;
    }
    
    public Pair<Colour, Colour> getTorchColours() {
        return new ImmutablePair<>(new Colour(0xFF9B26FF), new Colour(0xFF1F0CFF));
    }
    
    public TileType getCorridorTileType() {
        return TileType.TILE_CORRIDOR;
    }
}
