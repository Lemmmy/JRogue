package pw.lemmmy.jrogue.dungeon.generators;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.Tiles;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class DungeonGenerator {
	/***
	 * Temporary room positions used during generation
	 */
	public class Room {
		private int roomX;
		private int roomY;
		private int roomWidth;
		private int roomHeight;

		/***
		 * List of rooms this room should be connected to
		 */
		private List<Room> touching = new ArrayList<Room>();

		public Room(int roomX, int roomY, int roomWidth, int roomHeight) {
			this.roomX = roomX;
			this.roomY = roomY;
			this.roomWidth = roomWidth;
			this.roomHeight = roomHeight;
		}

		public int getRoomX() {
			return roomX;
		}

		public int getRoomY() {
			return roomY;
		}

		public int getCenterX() {
			return getRoomX() + (int) Math.floor(getRoomWidth() / 2);
		}

		public int getCenterY() {
			return getRoomY() + (int) Math.floor(getRoomHeight() / 2);
		}

		public int getRoomWidth() {
			return roomWidth;
		}

		public int getRoomHeight() {
			return roomHeight;
		}

		public List<Room> getTouching() {
			return touching;
		}

		public boolean addTouching(Room room) {
			return touching.add(room);
		}
	}

	protected List<Room> rooms = new ArrayList<>();
	protected Level level;

	protected ThreadLocalRandom rand = ThreadLocalRandom.current();

	public DungeonGenerator(Level level) {
		this.level = level;
	}

	// TODO: Returning List<Room> is temporary
	public abstract List<Room> generate();

	protected boolean canBuildRoom(int roomX, int roomY, int roomWidth, int roomHeight) {
		// the offsets are to prevent rooms directly touching each other

		for (int y = roomY - 2; y < roomY + roomHeight + 2; y++) {
			for (int x = roomX - 2; x < roomX + roomWidth + 2; x++) {
				if (level.getTile(x, y) == null || !level.getTile(x, y).isBuildable()) {
					return false;
				}
			}
		}

		return true;
	}

	protected Room buildRoom(int roomX, int roomY, int roomWidth, int roomHeight) {
		for (int y = roomY; y < roomY + roomHeight; y++) {
			for (int x = roomX; x < roomX + roomWidth; x++) {
				boolean wall = x == roomX || x == roomX + roomWidth - 1 || y == roomY || y == roomY + roomHeight - 1;

				level.setTile(x, y, wall ? Tiles.TILE_ROOM_WALL : Tiles.TILE_ROOM_FLOOR);
			}
		}

		Room room = new Room(roomX, roomY, roomWidth, roomHeight);
		rooms.add(room);
		return room;
	}

	protected void buildLine(int startX, int startY, int endX, int endY, Tiles tile, boolean buildableCheck) {
		float diffX = endX - startX;
		float diffY = endY - startY;

		float dist = Math.abs(diffX) + Math.abs(diffY);

		float dx = diffX / dist;
		float dy = diffY / dist;

		for (int i = 0; i <= Math.ceil(dist); i++) {
			int x = Math.round(startX + dx * i);
			int y = Math.round(startY + dy * i);

			if (level.getTile(x, y).isBuildable()) {
				level.setTile(x, y, tile);
			}
		}
	}

	protected int[][] getConnectionPoints(Room a, Room b) {
		int dx = Math.abs(b.getCenterX() - a.getCenterX());
		int dy = Math.abs(b.getCenterY() - a.getCenterY());

		if (dx > dy) {
			if (b.getCenterX() > a.getCenterX()) {
				return new int[][] {
					{a.getRoomX() + a.getRoomWidth() - 1, a.getCenterY()},
					{b.getRoomX(), b.getCenterY()}
				};
			} else {
				return new int[][] {
					{b.getRoomX() + b.getRoomWidth() - 1, b.getCenterY()},
					{a.getRoomX(), a.getCenterY()}
				};
			}
		} else {
			if (b.getCenterY() > a.getCenterY()) {
				return new int[][] {
					{a.getCenterX(), a.getRoomY() + a.getRoomHeight() - 1},
					{b.getCenterX(), b.getRoomY()}
				};
			} else {
				return new int[][] {
					{b.getCenterX(), b.getRoomY() + b.getRoomHeight() - 1},
					{a.getCenterX(), a.getRoomY()}
				};
			}
		}
	}

	protected void safePlaceDoor(int x, int y) {
		level.setTile(x, y, Tiles.TILE_ROOM_DOOR);

		for (int[] direction : Utils.DIRECTIONS) {
			int nx = x + direction[0];
			int ny = y + direction[1];

			if (level.getTile(nx, ny) == Tiles.TILE_EMPTY) {
				level.setTile(nx, ny, Tiles.TILE_CORRIDOR);
			}
		}
	}
}
