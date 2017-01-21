package pw.lemmmy.jrogue.dungeon.generators.rooms;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.generators.DungeonGenerator;
import pw.lemmmy.jrogue.dungeon.generators.GeneratorRooms;

import java.util.ArrayList;
import java.util.List;

public abstract class Room {
	private Level level;
	
	private int roomX;
	private int roomY;
	private int roomWidth;
	private int roomHeight;
	
	/***
	 * List of rooms this room should be connected to
	 */
	private List<Room> touching = new ArrayList<>();
	
	private List<DungeonGenerator.ConnectionPoint> connectionPoints = new ArrayList<>();
	private boolean isSpawn = false;
	
	public Room(Level level, int roomX, int roomY, int roomWidth, int roomHeight) {
		this.level = level;
		
		this.roomX = roomX;
		this.roomY = roomY;
		this.roomWidth = roomWidth;
		this.roomHeight = roomHeight;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public int getRoomX() {
		return roomX;
	}
	
	public int getCenterX() {
		return getRoomX() + (int) Math.floor(getWidth() / 2);
	}
	
	public int getWidth() {
		return roomWidth;
	}
	
	public int getRoomY() {
		return roomY;
	}
	
	public int getCenterY() {
		return getRoomY() + (int) Math.floor(getHeight() / 2);
	}
	
	public int getHeight() {
		return roomHeight;
	}
	
	public boolean addTouching(Room room) {
		return touching.add(room);
	}
	
	public List<Room> getTouching() {
		return touching;
	}
	
	public boolean addConnectionPoint(DungeonGenerator.ConnectionPoint point) {
		return connectionPoints.add(point);
	}
	
	public List<DungeonGenerator.ConnectionPoint> getConnectionPoints() {
		return connectionPoints;
	}
	
	public boolean isSpawn() {
		return isSpawn;
	}
	
	public void setSpawn() {
		this.isSpawn = true;
	}
	
	public abstract void build(GeneratorRooms generator);
	
	public abstract void addFeatures();
}
