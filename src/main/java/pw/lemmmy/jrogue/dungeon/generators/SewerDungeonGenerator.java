package pw.lemmmy.jrogue.dungeon.generators;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public class SewerDungeonGenerator extends RoomGenerator {
	public SewerDungeonGenerator(Level level, Tile sourceTile) {
		super(level, sourceTile);
	}
	
	@Override
	public boolean generate() {
		if (!super.generate()) {
			return false;
		}
		
		return verify();
	}
	
	@Override
	public Climate getClimate() {
		return Climate.MID;
	}
	
	@Override
	public TileType getTorchTileType() {
		return TileType.TILE_ROOM_WALL;
	}
	
	@Override
	protected void buildLCorridor(ConnectionPoint point) {
		int ax = point.getAX();
		int ay = point.getAY();
		
		int bx = point.getBX();
		int by = point.getBY();
		
		int dx = bx - ax;
		int dy = by - ay;
		
		TileType tile = TileType.TILE_ROOM_FLOOR;
		TileType tile2 = TileType.TILE_ROOM_WALL;
		
		if (Math.abs(dx) < 1 || Math.abs(dy) < 1) {
			buildLine(ax, ay, bx, by, tile, true, true);
			
			return;
		}
		
		buildLine(ax, ay, bx, ay, tile, true, true);
		buildLine(bx, ay, bx, by, tile, true, true);
	}
	
	@Override
	protected void buildSCorridor(ConnectionPoint point) {
		int ax = point.getAX();
		int ay = point.getAY();
		
		int bx = point.getBX();
		int by = point.getBY();
		
		int dx = bx - ax;
		int dy = by - ay;
		
		TileType tile = TileType.TILE_ROOM_FLOOR;
		TileType tile2 = TileType.TILE_ROOM_WALL;
		
		if (point.getIntendedOrientation() == Orientation.HORIZONTAL) {
			// kill me
			
			buildLine(ax, ay, ax + (int) Math.ceil(dx / 2), ay, tile, true, true);
			buildLine(ax + Math.round(dx / 2), ay, ax + (int) Math.floor(dx / 2), by, tile, true, true);
			buildLine(bx, by, ax + (int) Math.floor(dx / 2), by, tile, true, true);
			
			buildLine(ax, ay + 1, ax + (int) Math.ceil(dx / 2) + 1, ay + 1, tile2, true, false);
			buildLine(ax, ay - 1, ax + (int) Math.ceil(dx / 2) + 1, ay - 1, tile2, true, false);
			buildLine(ax + Math.round(dx / 2) + 1, ay, ax + (int) Math.floor(dx / 2) + 1, by, tile2, true, false);
			buildLine(ax + Math.round(dx / 2) - 1, ay, ax + (int) Math.floor(dx / 2) - 1, by, tile2, true, false);
			buildLine(bx, by + 1, ax + (int) Math.floor(dx / 2) - 1, by + 1, tile2, true, false);
			buildLine(bx, by - 1, ax + (int) Math.floor(dx / 2) - 1, by - 1, tile2, true, false);
		} else {
			buildLine(ax, ay, ax, ay + (int) Math.ceil(dy / 2), tile, true, true);
			buildLine(ax, ay + Math.round(dy / 2), bx, ay + (int) Math.floor(dy / 2), tile, true, true);
			buildLine(bx, by, bx, ay + (int) Math.floor(dy / 2), tile, true, true);
			
			buildLine(ax + 1, ay, ax + 1, ay + (int) Math.ceil(dy / 2), tile2, true, false);
			buildLine(ax - 1, ay, ax - 1, ay + (int) Math.ceil(dy / 2), tile2, true, false);
			buildLine(ax - 1, ay + Math.round(dy / 2) + 1, bx + 1, ay + (int) Math.floor(dy / 2) + 1, tile2, true, false);
			buildLine(ax - 1, ay + Math.round(dy / 2) - 1, bx + 1, ay + (int) Math.floor(dy / 2) - 1, tile2, true, false);
			buildLine(bx + 1, by, bx + 1, ay + (int) Math.floor(dy / 2), tile2, true, false);
			buildLine(bx - 1, by, bx - 1, ay + (int) Math.floor(dy / 2), tile2, true, false);
		}
	}
}
