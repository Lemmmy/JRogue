package pw.lemmmy.jrogue.dungeon.generators;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.Tiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class DungeonGenerator {
	/***
	 * Temporary room positions used during generation
	 */
	private class Room {
		private int roomX;
		private int roomY;
		private int roomWidth;
		private int roomHeight;

		/***
		 * List of rooms this room should be connected to
		 */
		private Room[] touching;

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

		public Room[] getTouching() {
			return touching;
		}
	}

	private List<Room> rooms = new ArrayList<>();

	protected Level level;

	protected Random rand = new Random();

	public DungeonGenerator(Level level) {
		this.level = level;
	}

	public abstract void generate();

	protected boolean canBuildRoom(int roomX, int roomY, int roomWidth, int roomHeight) {
		for (int y = roomY; y < roomY + roomHeight; y++) {
			for (int x = roomX; x < roomX + roomWidth; x++) {
				if (!level.getTile(x, y).isBuildable()) {
					return false;
				}
			}
		}

		return true;
	}

	protected Room buildRoom(int roomX, int roomY, int roomWidth, int roomHeight) {
		for (int y = roomY; y < roomY + roomHeight; y++) {
			for (int x = roomX; x < roomX + roomWidth; x++) {
				boolean wall = x == roomX || x == roomX + roomWidth || y == roomY || y == roomY + roomHeight;

				level.setTile(x, y, wall ? Tiles.TILE_ROOM_WALL : Tiles.TILE_ROOM_FLOOR);
			}
		}

		Room room = new Room(roomX, roomY, roomWidth, roomHeight);
		rooms.add(room);
		return room;
	}
}
