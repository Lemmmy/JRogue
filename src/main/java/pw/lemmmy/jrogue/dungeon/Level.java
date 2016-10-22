package pw.lemmmy.jrogue.dungeon;

import pw.lemmmy.jrogue.utils.Utils;

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

		Arrays.fill(tiles, Tiles.TILE_EMPTY);
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
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return null;
		}

		return tiles[width * y + x];
	}

	public void setTile(int x, int y, Tiles tile) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return;
		}

		tiles[width * y + x] = tile;
	}

	public Tiles[] getAdjacentTiles(int x, int y) {
		Tiles[] t = new Tiles[Utils.DIRECTIONS.length];

		for (int i = 0; i < Utils.DIRECTIONS.length; i++) {
			int[] direction = Utils.DIRECTIONS[i];

			t[i] = getTile(x + direction[0], y + direction[1]);
		}

		return t;
	}
}
