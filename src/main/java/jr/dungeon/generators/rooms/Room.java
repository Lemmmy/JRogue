package jr.dungeon.generators.rooms;

import jr.dungeon.Level;
import jr.dungeon.generators.DungeonGenerator;
import jr.dungeon.generators.GeneratorRooms;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Room {
	private Level level;
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Room(Level level, int x, int y, int width, int height) {
		this.level = level;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	/***
	 * List of rooms this room should be connected to
	 */
	private List<Room> touching = new ArrayList<>();
	
	private List<GeneratorRooms.ConnectionPoint> connectionPoints = new ArrayList<>();
	
	@Setter private boolean spawn = false;
	
	public int getCenterX() {
		return getX() + (int) Math.floor(getWidth() / 2);
	}
	
	public int getCenterY() {
		return getY() + (int) Math.floor(getHeight() / 2);
	}
	
	public boolean addTouching(Room room) {
		return touching.add(room);
	}
	
	public boolean addConnectionPoint(GeneratorRooms.ConnectionPoint point) {
		return connectionPoints.add(point);
	}
	
	public abstract void build(GeneratorRooms generator);
	
	public abstract void addFeatures();
}
