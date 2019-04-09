package jr.rendering.utils;

import jr.dungeon.TileStore;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;

import java.util.function.Predicate;

@SuppressWarnings("Duplicates")
public class BlobUtils {
	public static int getPositionMask4(Tile tile, Point p, Predicate<TileType> isJoinedTile) {
		TileStore ts = tile.getLevel().tileStore;
		
		int n = isJoinedTile.test(ts.getTileTypeRaw(p.x, p.y + 1)) ? 1 : 0;
		int s = isJoinedTile.test(ts.getTileTypeRaw(p.x, p.y - 1)) ? 1 : 0;
		int w = isJoinedTile.test(ts.getTileTypeRaw(p.x - 1, p.y)) ? 1 : 0;
		int e = isJoinedTile.test(ts.getTileTypeRaw(p.x + 1, p.y)) ? 1 : 0;
		
		return n + 2 * e + 4 * s + 8 * w;
	}
	
	public static int getPositionMask8(Tile tile, Point p, Predicate<TileType> isJoinedTile) {
		TileStore ts = tile.getLevel().tileStore;
		
		int n = isJoinedTile.test(ts.getTileTypeRaw(p.x, p.y + 1)) ? 1 : 0;
		int s = isJoinedTile.test(ts.getTileTypeRaw(p.x, p.y - 1)) ? 1 : 0;
		int w = isJoinedTile.test(ts.getTileTypeRaw(p.x - 1, p.y)) ? 1 : 0;
		int e = isJoinedTile.test(ts.getTileTypeRaw(p.x + 1, p.y)) ? 1 : 0;
		
		int nw = isJoinedTile.test(ts.getTileTypeRaw(p.x - 1, p.y + 1)) && w == 1 && n == 1 ? 1 : 0;
		int ne = isJoinedTile.test(ts.getTileTypeRaw(p.x + 1, p.y + 1)) && e == 1 && n == 1 ? 1 : 0;
		int sw = isJoinedTile.test(ts.getTileTypeRaw(p.x - 1, p.y - 1)) && w == 1 && s == 1 ? 1 : 0;
		int se = isJoinedTile.test(ts.getTileTypeRaw(p.x + 1, p.y - 1)) && e == 1 && s == 1 ? 1 : 0;
		
		return nw + 2 * n + 4 * ne + 8 * w + 16 * e + 32 * sw + 64 * s + 128 * se;
	}
}