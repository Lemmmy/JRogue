package pw.lemmmy.jrogue.dungeon;

import java.util.Arrays;

public class Level {
	public Tiles[] tiles;

	/***
	 * Tiles the player thinks exist
	 */
	public boolean[] discoveredTiles;

	/***
	 * Tiles visible this turn
	 */
	public boolean[] visibleTiles;

	private int width;
	private int height;

	/**
	 * The "level" of this floor - how deep it is in the dungeon and ground
	 */
	private int depth;

	public Level(int width, int height, int depth) {
		this.width = width;
		this.height = height;

		this.depth = depth;

		tiles = new Tiles[width * height];
		discoveredTiles = new boolean[width * height];
		visibleTiles = new boolean[width * height];

		Arrays.fill(tiles, Tiles.TILE_GROUND);
		Arrays.fill(discoveredTiles, false);
		Arrays.fill(visibleTiles, false);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getDepth() {
		return depth;
	}

	public Tiles getTile(int x, int y) {
		return tiles[width * y + x];
	}

	public Tiles setTile(int x, int y, Tiles tile) {
		return tiles[width * y + x] = tile;
	}

	public Tiles[] getAdjacentTiles(int x, int y) {
		return new Tiles[] {
			getTile(x + 1, y),
			getTile(x - 1, y),
			getTile(x, y + 1),
			getTile(x, y - 1)
		};
	}
}
