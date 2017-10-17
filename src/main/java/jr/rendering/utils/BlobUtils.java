package jr.rendering.utils;

import jr.dungeon.Level;
import jr.dungeon.tiles.TileType;

import java.util.function.Predicate;

public class BlobUtils {
	public static int getPositionMask4(Predicate<TileType> isJoinedTile, Level level, int x, int y) {
		int n = isJoinedTile.test(level.tileStore.getTileType(x, y - 1)) ? 1 : 0;
		int s = isJoinedTile.test(level.tileStore.getTileType(x, y + 1)) ? 1 : 0;
		int w = isJoinedTile.test(level.tileStore.getTileType(x - 1, y)) ? 1 : 0;
		int e = isJoinedTile.test(level.tileStore.getTileType(x + 1, y)) ? 1 : 0;
		
		return n + 2 * e + 4 * s + 8 * w;
	}
	
	public static int getPositionMask8(Predicate<TileType> isJoinedTile, Level level, int x, int y) {
		int n = isJoinedTile.test(level.tileStore.getTileType(x, y - 1)) ? 1 : 0;
		int s = isJoinedTile.test(level.tileStore.getTileType(x, y + 1)) ? 1 : 0;
		int w = isJoinedTile.test(level.tileStore.getTileType(x - 1, y)) ? 1 : 0;
		int e = isJoinedTile.test(level.tileStore.getTileType(x + 1, y)) ? 1 : 0;
		
		int nw = isJoinedTile.test(level.tileStore.getTileType(x - 1, y - 1)) && w == 1 && n == 1 ? 1 : 0;
		int ne = isJoinedTile.test(level.tileStore.getTileType(x + 1, y - 1)) && e == 1 && n == 1 ? 1 : 0;
		int sw = isJoinedTile.test(level.tileStore.getTileType(x - 1, y + 1)) && w == 1 && s == 1 ? 1 : 0;
		int se = isJoinedTile.test(level.tileStore.getTileType(x + 1, y + 1)) && e == 1 && s == 1 ? 1 : 0;
		
		return nw + 2 * n + 4 * ne + 8 * w + 16 * e + 32 * sw + 64 * s + 128 * se;
	}
}
