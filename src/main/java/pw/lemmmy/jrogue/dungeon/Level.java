package pw.lemmmy.jrogue.dungeon;

public class Level {
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

	private Tiles[] tiles;

	/***
	 * Tiles the player thinks exist
	 */
	private boolean[] discoveredTiles;

	/***
	 * Tiles visible this turn
	 */
	private boolean[] visibleTiles;

	/**
	 * The "level" of this floor - how deep it is in the dungeon and ground
	 */
	private int depth;
}
