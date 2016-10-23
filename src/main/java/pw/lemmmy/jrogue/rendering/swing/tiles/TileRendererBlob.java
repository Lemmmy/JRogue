package pw.lemmmy.jrogue.rendering.swing.tiles;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.Tiles;
import pw.lemmmy.jrogue.rendering.swing.utils.ImageLoader;
import pw.lemmmy.jrogue.utils.Utils;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public abstract class TileRendererBlob extends TileRenderer  {
	private static final int BLOB_SHEET_WIDTH = 8;
	private static final int BLOB_SHEET_HEIGHT = 6;

	private static final int[][] LOCATIONS = {
		{2, 1}, {8, 2}, {10, 3}, {11, 4}, {16, 5}, {18, 6}, {22, 7}, {24, 8}, {26, 9}, {27, 10}, {30, 11}, {31, 12},
		{64, 13}, {66, 14}, {72, 15}, {74, 16}, {75, 17}, {80, 18}, {82, 19}, {86, 20}, {88, 21}, {90, 22}, {91, 23},
		{94, 24}, {95, 25}, {104, 26}, {106, 27}, {107, 28}, {120, 29}, {122, 30}, {123, 31}, {126, 32}, {127, 33},
		{208, 34}, {210, 35}, {214, 36}, {216, 37}, {218, 38}, {219, 39}, {222, 40}, {223, 41}, {248, 42}, {250, 43},
		{251, 44}, {254, 45}, {255, 46}, {0, 47}
	};

	private static final int[] MAP = new int[256];

	static {
		Arrays.fill(MAP, 0);

		for (int[] location : LOCATIONS) {
			MAP[location[0]] = location[1];
		}
	}

	protected BufferedImage[] images = new BufferedImage[BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT];

	public TileRendererBlob() {
		this(0, 0);
	}

	public TileRendererBlob(int blobOffsetX, int blobOffsetY) {
		BufferedImage sheet = ImageLoader.getImage("blobs.png");

		if (sheet == null) {
			JRogue.getLogger().fatal("Failed to load spritesheet {}.", "blobs.png");
			System.exit(1);
		}

		for (int i = 0; i < BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT; i++) {
			int sheetX = (i % BLOB_SHEET_WIDTH) + (BLOB_SHEET_WIDTH * blobOffsetX);
			int sheetY = (int) Math.floor(i / BLOB_SHEET_WIDTH) + (BLOB_SHEET_HEIGHT * blobOffsetY);

			images[i] = sheet.getSubimage(sheetX * TileMap.TILE_WIDTH, sheetY * TileMap.TILE_HEIGHT, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
		}
	}

	abstract boolean isJoinedTile(Tiles tile);

	protected int getPositionMask(Level level, int x, int y) {
		int n = (isJoinedTile(level.getTile(x, y - 1))) ? 1 : 0;
		int s = (isJoinedTile(level.getTile(x, y + 1))) ? 1 : 0;
		int w = (isJoinedTile(level.getTile(x - 1, y))) ? 1 : 0;
		int e = (isJoinedTile(level.getTile(x + 1, y))) ? 1 : 0;

		int nw = (isJoinedTile(level.getTile(x - 1, y - 1)) && w == 1 && n == 1) ? 1 : 0;
		int ne = (isJoinedTile(level.getTile(x + 1, y - 1)) && e == 1 && n == 1) ? 1 : 0;
		int sw = (isJoinedTile(level.getTile(x - 1, y + 1)) && w == 1 && s == 1) ? 1 : 0;
		int se = (isJoinedTile(level.getTile(x + 1, y + 1)) && e == 1 && s == 1) ? 1 : 0;

		return nw + 2 * n + 4 * ne + 8 * w + 16 * e + 32 * sw + 64 * s + 128 * se;
	}

	protected BufferedImage getImageFromMask(int mask) {
		return images[MAP[mask]];
	}
}
