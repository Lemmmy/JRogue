package jr.dungeon.generators.rooms;

import jr.dungeon.Level;
import jr.dungeon.generators.DungeonGenerator;
import jr.dungeon.generators.GeneratorRooms;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class Room {
	@NonNull private Level level;
	
	@NonNull private int x;
	@NonNull private int y;
	@NonNull private int width;
	@NonNull private int height;
	
	/***
	 * List of rooms this room should be connected to
	 */
	private List<Room> touching = new ArrayList<>();
	
	private List<DungeonGenerator.ConnectionPoint> connectionPoints = new ArrayList<>();
	
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
	
	public boolean addConnectionPoint(DungeonGenerator.ConnectionPoint point) {
		return connectionPoints.add(point);
	}
	
	public abstract void build(GeneratorRooms generator);
	
	public abstract void addFeatures();
}
