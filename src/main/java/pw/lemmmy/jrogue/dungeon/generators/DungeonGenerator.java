package pw.lemmmy.jrogue.dungeon.generators;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.Tiles;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class DungeonGenerator {
	protected List<Room> rooms = new ArrayList<>();
	protected Level level;
	protected Random rand = new Random();

	public DungeonGenerator(Level level) {
		this.level = level;
	}

	public abstract void generate();

	protected int nextInt(int min, int max) {
		return rand.nextInt(max - min) + min;
	}

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

	protected void buildLine(int startX, int startY, int endX, int endY, Tiles tile, boolean buildableCheck, boolean buildDoors) {
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
			} else if (buildDoors && canPlaceDoor(x, y)) {
				safePlaceDoor(x, y);
			}
		}
	}

	public boolean canPlaceDoor(int x, int y) {
		if (level.getTile(x, y) == Tiles.TILE_ROOM_WALL) {
			Tiles[] adjacentTiles = level.getAdjacentTiles(x, y);

			for (Tiles tile : adjacentTiles) {
				if (tile == Tiles.TILE_ROOM_DOOR) {
					return false;
				}
			}

			return getWallOrientation(adjacentTiles) != Orientation.CORNER;
		}

		return false;
	}

	protected Orientation getWallOrientation(int x, int y) {
		return getWallOrientation(level.getAdjacentTiles(x, y));
	}

	protected Orientation getWallOrientation(Tiles[] adjacentTiles) {
		boolean h = adjacentTiles[0] == Tiles.TILE_ROOM_WALL || adjacentTiles[1] == Tiles.TILE_ROOM_WALL;
		boolean v = adjacentTiles[2] == Tiles.TILE_ROOM_WALL || adjacentTiles[3] == Tiles.TILE_ROOM_WALL;

		if (h && !v) {
			return Orientation.HORIZONTAL;
		} else if (!h && v) {
			return Orientation.VERTICAL;
		} else {
			return Orientation.CORNER;
		}
	}

	protected ConnectionPoint getConnectionPoint(Room a, Room b) {
		int dx = Math.abs(b.getCenterX() - a.getCenterX());
		int dy = Math.abs(b.getCenterY() - a.getCenterY());

		if (dx > dy) {
			if (dx <= 5 || b.getCenterX() < a.getCenterX() || a.getRoomX() + a.getRoomWidth() >= b.getRoomX() || b.getRoomX() + b.getRoomWidth() <= a.getRoomX()) {
				if (b.getRoomX() + b.getRoomWidth() > a.getRoomX() + a.getRoomWidth()) {
					return new ConnectionPoint(
						a.getRoomX() + a.getRoomWidth() - 1, a.getCenterY(),
						b.getCenterX(), b.getRoomY() + b.getRoomHeight() - 1,
						Orientation.HORIZONTAL
					);
				} else {
					return new ConnectionPoint(
						b.getRoomX() + b.getRoomWidth() - 1, b.getCenterY(),
						a.getCenterX(), a.getRoomY() + a.getRoomHeight() - 1,
						Orientation.HORIZONTAL
					);
				}
			} else {
				if (b.getRoomX() > a.getRoomX() || b.getRoomX() + b.getRoomWidth() > a.getRoomX() + a.getRoomWidth()) {
					return new ConnectionPoint(
						a.getRoomX() + a.getRoomWidth() - 1, a.getCenterY(),
						b.getRoomX(), b.getCenterY(),
						Orientation.HORIZONTAL
					);
				} else {
					return new ConnectionPoint(
						b.getRoomX() + b.getRoomWidth() - 1, b.getCenterY(),
						a.getRoomX(), a.getCenterY(),
						Orientation.HORIZONTAL
					);
				}
			}
		} else {
			if (dy <= 5 || (b.getCenterX() - a.getCenterX() < 0) || a.getRoomY() + a.getRoomHeight() == b.getRoomY() || b.getRoomY() + b.getRoomHeight() == a.getRoomY()) {
				if (b.getRoomY() + b.getRoomHeight() > a.getRoomY() + a.getRoomHeight()) {
					return new ConnectionPoint(
						a.getCenterX(), a.getRoomY() + a.getRoomHeight() - 1,
						b.getRoomX() + b.getRoomWidth() - 1, b.getCenterY(),
						Orientation.VERTICAL
					);
				} else {
					return new ConnectionPoint(
						b.getCenterX(), b.getRoomY() + b.getRoomHeight() - 1,
						a.getRoomX() + a.getRoomWidth() - 1, a.getCenterY(),
						Orientation.VERTICAL
					);
				}
			} else {
				if (b.getRoomY() + b.getRoomHeight() > a.getRoomY() + a.getRoomHeight()) {
					return new ConnectionPoint(
						a.getCenterX(), a.getRoomY() + a.getRoomHeight() - 1,
						b.getCenterX(), b.getRoomY(),
						Orientation.VERTICAL
					);
				} else {
					return new ConnectionPoint(
						b.getCenterX(), b.getRoomY() + b.getRoomHeight() - 1,
						a.getCenterX(), a.getRoomY(),
						Orientation.VERTICAL
					);
				}
			}
		}
	}

	protected void safePlaceDoor(int x, int y) {
		level.setTile(x, y, Tiles.TILE_ROOM_DOOR);

		for (int[] direction : Utils.DIRECTIONS) {
			int nx = x + direction[0];
			int ny = y + direction[1];

			Tiles t = level.getTile(nx, ny);

			if (t == Tiles.TILE_EMPTY) {
				level.setTile(nx, ny, Tiles.TILE_CORRIDOR);
			} else if (t == Tiles.TILE_ROOM_WATER) {
				level.setTile(nx, ny, Tiles.TILE_ROOM_FLOOR);
			}
		}
	}

	protected enum Orientation {
		HORIZONTAL,
		VERTICAL,
		CORNER
	}

	protected class ConnectionPoint {
		private int ax, ay;
		private int bx, by;

		private Orientation intendedOrientation;
		private Orientation orientationA;
		private Orientation orientationB;

		private Tiles debugTile;

		public ConnectionPoint(int ax, int ay, int bx, int by, Orientation intendedOrientation) {
			this(ax, ay, bx, by, intendedOrientation, null);
		}

		public ConnectionPoint(int ax, int ay, int bx, int by, Orientation intendedOrientation, Tiles debugTile) {
			this.ax = ax;
			this.ay = ay;
			this.bx = bx;
			this.by = by;

			this.intendedOrientation = intendedOrientation;
			this.orientationA = getWallOrientation(ax, ay);
			this.orientationB = getWallOrientation(bx, by);

			this.debugTile = debugTile;
		}

		public int getAX() {
			return ax;
		}

		public int getAY() {
			return ay;
		}

		public int getBX() {
			return bx;
		}

		public int getBY() {
			return by;
		}

		public Orientation getIntendedOrientation() {
			return intendedOrientation;
		}

		public Orientation getOrientationA() {
			return orientationA;
		}

		public Orientation getOrientationB() {
			return orientationB;
		}

		public Tiles getDebugTile() {
			return debugTile;
		}
	}

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
}
