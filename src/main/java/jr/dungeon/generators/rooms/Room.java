package jr.dungeon.generators.rooms;

import jr.dungeon.Level;
import jr.dungeon.TileStore;
import jr.dungeon.generators.GeneratorRooms;
import jr.utils.Point;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

import static jr.utils.QuickMaths.ifloor;

/**
 * Structure containing a room's geometry and letting it build its features in the level.
 */
public abstract class Room {
    /**
     * The {@link Level} this room is part of.
     */
    public final Level level;
    
    protected final TileStore tileStore;
    
    /**
     * The position of the room's bottom-left corner.
     */
    public final Point position;
    /**
     * The width of the room.
     */
    public final int width;
    /**
     * The height of the room.
     */
    public final int height;
    
    /***
     * List of rooms this room should be connected to in the room graph.
     */
    @Getter private List<Room> connectedRooms = new LinkedList<>();
    
    /**
     * List of {@link jr.dungeon.generators.GeneratorRooms.ConnectionPoint connection points} involving this room -
     * points a door should be placed and a corridor built from.
     */
    @Getter private List<GeneratorRooms.ConnectionPoint> connectionPoints = new LinkedList<>();
    
    /**
     * Whether or not this room is the room the player spawns in.
     */
    @Getter @Setter private boolean spawn = false;
    
    /**
     * @param level The {@link Level} this room is part of.
     * @param position The position of the room's bottom-left corner.
     * @param width The width of the room.
     * @param height The height of the room.
     */
    public Room(Level level, Point position, int width, int height) {
        this.level = level;
        this.tileStore = level.tileStore;
        
        this.position = position;
        this.width = width;
        this.height = height;
    }
    
    public Point getCenter() {
        return Point.get(
            position.x + ifloor(width / 2),
            position.y + ifloor(height / 2)
        );
    }
    
    public abstract Point randomPoint();
    public abstract Point randomPointAlongWall(WallSide side);
    public abstract Point doorPointAlongWall(WallSide side);
    
    /**
     * Returns whether or not the given {@link Point} is on or outside the edge of the room.
     *
     * @param point The {@link Point} to check.
     * @return Whether or not the {@link Point} is on or outside the edge of this room.
     */
    public boolean isEdgePoint(Point point) {
        return point.x == position.x || point.x == position.x + width  - 1 ||
               point.y == position.y || point.y == position.y + height - 1;
    }
    
    /**
     * Returns whether or not the given {@link Point} is on the outside corner of this room.
     *
     * @param point The {@link Point} to check.
     * @return Whether or not the {@link Point} is on the outside corner of this room.
     */
    public boolean isCornerPoint(Point point) {
        return (point.x == position.x || point.x == position.x + width  - 1) &&
               (point.y == position.y || point.y == position.y + height - 1);
    }
    
    /**
     * Add a room to the list of rooms this room is connected to in the room graph.
     *
     * @param room The other room this room should be connected to.
     *
     * @return {@code true} if the collection changed as a result of this call (as specified by
     * {@link java.util.Collection#add(Object)}.
     */
    public boolean addConnected(Room room) {
        return connectedRooms.add(room);
    }
    
    /**
     * Add a connection point to the list of
     * {@link jr.dungeon.generators.GeneratorRooms.ConnectionPoint connection points} involving this room - points a
     * door should be placed and a corridor built from.
     *
     * @param point The connection point to add.
     *
     * @return {@code true} if the collection changed as a result of this call (as specified by
     * {@link java.util.Collection#add(Object)}.
     */
    public boolean addConnectionPoint(GeneratorRooms.ConnectionPoint point) {
        return connectionPoints.add(point);
    }
    
    /**
     * Build this room's basic structure in the {@link Level}.
     *
     * @param generator The generator that is generating this room.
     */
    public abstract void build(GeneratorRooms generator);
    
    /**
     * Add special features (like {@link jr.dungeon.entities.Entity Entities} to this room.
     */
    public abstract void addFeatures();
    
    public enum WallSide {
        TOP, BOTTOM, LEFT, RIGHT
    }
}
