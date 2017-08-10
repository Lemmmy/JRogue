package jr.dungeon.generators.rooms;

import jr.dungeon.Level;
import jr.dungeon.generators.GeneratorRooms;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Structure containing a room's geometry and letting it build its features in the level.
 */
@Getter
public abstract class Room {
	/**
	 * The {@link Level} this room is part of.
	 */
	private Level level;
	
	/**
	 * The X position of the room's top-left corner.
	 */
	private int x;
	/**
	 * The Y position of the room's top-right corner.
	 */
	private int y;
	/**
	 * The width of the room.
	 */
	private int width;
	/**
	 * The height of the room.
	 */
	private int height;
	
	/***
	 * List of rooms this room should be connected to.
	 *
	 * @see GeneratorRooms#graphRooms()
	 */
	private List<Room> touching = new LinkedList<>();
	
	/**
	 * List of {@link jr.dungeon.generators.GeneratorRooms.ConnectionPoint connection points} involving this room -
	 * points a door should be placed and a corridor built from.
	 */
	private List<GeneratorRooms.ConnectionPoint> connectionPoints = new LinkedList<>();
	
	/**
	 * Whether or not this room is the room the player spawns in.
	 */
	@Setter private boolean spawn = false;
	
	/**
	 * @param level The {@link Level} this room is part of.
	 * @param x The X position of the room's top-left corner.
	 * @param y The Y position of the room's top-left corner.
	 * @param width The width of the room.
	 * @param height The height of the room.
	 */
	public Room(Level level, int x, int y, int width, int height) {
		this.level = level;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public int getCenterX() {
		return getX() + (int) Math.floor(getWidth() / 2);
	}
	
	public int getCenterY() {
		return getY() + (int) Math.floor(getHeight() / 2);
	}
	
	/**
	 * Add a room to the list of rooms this room should be connected to during the
	 * {@link GeneratorRooms#graphRooms() graphing step}.
	 *
	 * @param room The other room this room should be connected to.
	 *
	 * @return <tt>true</tt> if the collection changed as a result of this call (as specified by
	 * {@link java.util.Collection#add(Object)}.
	 */
	public boolean addTouching(Room room) {
		return touching.add(room);
	}
	
	/**
	 * Add a connection point to the list of
	 * {@link jr.dungeon.generators.GeneratorRooms.ConnectionPoint connection points} involving this room - points a
	 * door should be placed and a corridor built from.
	 *
	 * @param point The connection point to add.
	 *
	 * @return <tt>true</tt> if the collection changed as a result of this call (as specified by
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
}
