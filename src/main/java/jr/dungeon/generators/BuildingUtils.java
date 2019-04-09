package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.TileStore;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;
import jr.utils.VectorInt;

public final class BuildingUtils {
	/**
	 * Places a line of tiles.
	 *
	 * @param tileStore The {@link TileStore} of the {@link Level} to modify.
	 * @param start The starting position of the line.
	 * @param end The ending position of the line.
	 * @param builder {@link TileBuilder} that takes in a {@link Tile}, and returns the {@link TileType} to place there.
	 */
	public static void buildLine(TileStore tileStore, Point start, Point end, BuildingUtils.TileBuilder builder) {
		VectorInt diff = VectorInt.between(start, end);
		
		float dist = Math.abs(diff.x) + Math.abs(diff.y);
		
		float dx = diff.x / dist;
		float dy = diff.y / dist;
		
		for (int i = 0; i <= Math.ceil(dist); i++) {
			builder.runBuilder(tileStore, Point.get(
				Math.round(start.x + dx * i),
				Math.round(start.y + dy * i)
			));
		}
	}
	
	/**
	 * Fills an area with tiles.
	 *
	 * @param tileStore The {@link TileStore} of the {@link Level} to modify.
	 * @param p The starting position of the line.
	 * @param width The width of the area, in tiles.
	 * @param height The height of the area, in tiles.
	 * @param builder {@link TileBuilder} that takes in a {@link Tile}, and returns the {@link TileType} to place there.
	 */
	public static void buildArea(TileStore tileStore, Point p, int width, int height, BuildingUtils.TileBuilder builder) {
		for (int y = p.y; y < p.y + height; y++) {
			for (int x = p.x; x < p.x + width; x++) {
				builder.runBuilder(tileStore, Point.get(x, y));
			}
		}
	}
	
	/**
	 * Fills an area with tiles.
	 *
	 * @param tileStore The {@link TileStore} of the {@link Level} to modify.
	 * @param start The starting position of the line.
	 * @param end The end position of the area.
	 * @param builder {@link TileBuilder} that takes in a {@link Tile}, and returns the {@link TileType} to place there.
	 *
	 * @see #buildArea(TileStore, Point, int, int, TileBuilder)
	 */
	public static void buildArea(TileStore tileStore, Point start, Point end, BuildingUtils.TileBuilder builder) {
		VectorInt size = VectorInt.between(start, end);
		buildArea(tileStore, start, size.x, size.y, builder);
	}
	
	/**
	 * Fills an area with tiles.
	 *
	 * @param tileStore The {@link TileStore} of the {@link Level} to modify.
	 * @param start The starting position of the line.
	 * @param width The width of the area, in tiles.
	 * @param height The height of the area, in tiles.
	 * @param tile The {@link TileType} to fill the area with.
	 *
	 * @see #buildArea(TileStore, Point, int, int, TileBuilder)
	 */
	public static void fillArea(TileStore tileStore, Point start, int width, int height, TileType tile) {
		buildArea(tileStore, start, width, height, BuildingUtils.TileBuilder.constant(tile));
	}
	
	/**
	 * Fills an area with tiles.
	 *
	 * @param tileStore The {@link TileStore} of the {@link Level} to modify.
	 * @param start The starting position of the line.
	 * @param end The end position of the area.
	 * @param tile The {@link TileType} to fill the area with.
	 *
	 * @see #buildArea(TileStore, Point, int, int, TileBuilder)
	 */
	public static void fillArea(TileStore tileStore, Point start, Point end, TileType tile) {
		buildArea(tileStore, start, end, BuildingUtils.TileBuilder.constant(tile));
	}
	
	/**
	 *
	 */
	@FunctionalInterface
	public interface TileBuilder {
		TileType build(Tile tile, Point position);
		
		default void runBuilder(TileStore tileStore, Point point) {
			Tile tile = tileStore.getTile(point);
			TileType result = build(tile, point);
			
			if (result != null) {
				tileStore.setTileType(point, result);
			}
		}
		
		static TileBuilder constant(TileType type) {
			return (t, p) -> type;
		}
	}
}
