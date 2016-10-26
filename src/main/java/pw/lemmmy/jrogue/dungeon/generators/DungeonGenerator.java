package pw.lemmmy.jrogue.dungeon.generators;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.TileType;
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

	public abstract boolean generate();

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

				if (wall) {
					if (x > roomX && x < roomX + roomWidth - 1 && x % 5 == 0) {
						level.setTile(x, y, TileType.TILE_ROOM_TORCH_FIRE);
					} else {
						level.setTile(x, y, TileType.TILE_ROOM_WALL);
					}
				} else {
					level.setTile(x, y, TileType.TILE_ROOM_FLOOR);
				}
			}
		}

		Room room = new Room(roomX, roomY, roomWidth, roomHeight);
		rooms.add(room);
		return room;
	}

	protected void buildLine(int startX, int startY, int endX, int endY, TileType tile, boolean buildableCheck, boolean buildDoors) {
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
		if (level.getTile(x, y) == TileType.TILE_ROOM_WALL) {
			TileType[] adjacentTiles = level.getAdjacentTiles(x, y);

			for (TileType tile : adjacentTiles) {
				if (tile == TileType.TILE_ROOM_DOOR_CLOSED) {
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

	protected Orientation getWallOrientation(TileType[] adjacentTiles) {
		boolean h = adjacentTiles[0].isWallType() || adjacentTiles[1].isWallType();
		boolean v = adjacentTiles[2].isWallType() || adjacentTiles[3].isWallType();

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
		level.setTile(x, y, TileType.TILE_ROOM_DOOR_CLOSED);

		for (int[] direction : Utils.DIRECTIONS) {
			int nx = x + direction[0];
			int ny = y + direction[1];

			TileType t = level.getTile(nx, ny);

			if (t == TileType.TILE_GROUND) {
				level.setTile(nx, ny, TileType.TILE_CORRIDOR);
			} else if (t == TileType.TILE_ROOM_WATER) {
				level.setTile(nx, ny, TileType.TILE_ROOM_FLOOR);
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

		private TileType debugTile;

		public ConnectionPoint(int ax, int ay, int bx, int by, Orientation intendedOrientation) {
			this(ax, ay, bx, by, intendedOrientation, null);
		}

		public ConnectionPoint(int ax, int ay, int bx, int by, Orientation intendedOrientation, TileType debugTile) {
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

		public TileType getDebugTile() {
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
		private List<Room> touching = new ArrayList<>();

		private List<ConnectionPoint> connectionPoints = new ArrayList<>();
		private boolean isSpawn;

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

		public List<ConnectionPoint> getConnectionPoints() {
			return connectionPoints;
		}

		public boolean addConnectionPoint(ConnectionPoint point) {
			return connectionPoints.add(point);
		}

		@Override
		public String toString() {
			return String.format(
				"x: %d y: %d %dx%d %d touching %d connection point(s)",
				getRoomX(),
				getRoomY(),
				getRoomWidth(),
				getRoomHeight(),
				getTouching().size(),
				getConnectionPoints().size()
			);
		}

		public boolean isSpawn() {
			return isSpawn;
		}

		public void setSpawn(boolean spawn) {
			this.isSpawn = spawn;
		}
	}
}
