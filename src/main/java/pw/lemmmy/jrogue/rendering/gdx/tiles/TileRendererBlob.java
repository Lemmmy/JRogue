package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

import java.util.Arrays;

public abstract class TileRendererBlob extends TileRenderer {
	protected static final int BLOB_SHEET_WIDTH = 8;
	protected static final int BLOB_SHEET_HEIGHT = 6;

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

	protected TextureRegion[] images = new TextureRegion[BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT];

	public TileRendererBlob() {
		this(0, 0);
	}

	public TileRendererBlob(int blobOffsetX, int blobOffsetY) {
		loadBlob(images, blobOffsetX, blobOffsetY);
	}

	protected void loadBlob(TextureRegion[] set, int blobOffsetX, int blobOffsetY) {
		for (int i = 0; i < BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT; i++) {
			int sheetX = (i % BLOB_SHEET_WIDTH) + (BLOB_SHEET_WIDTH * blobOffsetX);
			int sheetY = (int) Math.floor(i / BLOB_SHEET_WIDTH) + (BLOB_SHEET_HEIGHT * blobOffsetY);

			set[i] = getImageFromSheet("blobs.png", sheetX, sheetY);
		}
	}

	protected int getPositionMask(Level level, int x, int y) {
		int n = (isJoinedTile(level.getTileType(x, y - 1))) ? 1 : 0;
		int s = (isJoinedTile(level.getTileType(x, y + 1))) ? 1 : 0;
		int w = (isJoinedTile(level.getTileType(x - 1, y))) ? 1 : 0;
		int e = (isJoinedTile(level.getTileType(x + 1, y))) ? 1 : 0;

		int nw = (isJoinedTile(level.getTileType(x - 1, y - 1)) && w == 1 && n == 1) ? 1 : 0;
		int ne = (isJoinedTile(level.getTileType(x + 1, y - 1)) && e == 1 && n == 1) ? 1 : 0;
		int sw = (isJoinedTile(level.getTileType(x - 1, y + 1)) && w == 1 && s == 1) ? 1 : 0;
		int se = (isJoinedTile(level.getTileType(x + 1, y + 1)) && e == 1 && s == 1) ? 1 : 0;

		return nw + 2 * n + 4 * ne + 8 * w + 16 * e + 32 * sw + 64 * s + 128 * se;
	}

	abstract boolean isJoinedTile(TileType tile);

	protected TextureRegion getImageFromMask(int mask) {
		return getImageFromMask(images, mask);
	}

	protected TextureRegion getImageFromMask(TextureRegion[] set, int mask) {
		return set[MAP[mask]];
	}
}
