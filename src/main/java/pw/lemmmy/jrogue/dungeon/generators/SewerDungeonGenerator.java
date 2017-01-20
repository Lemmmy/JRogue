package pw.lemmmy.jrogue.dungeon.generators;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.OpenSimplexNoise;

public class SewerDungeonGenerator extends RoomGenerator {
	private static final double THRESHOLD_WATER_NOISE = 0.3;
	private static final double SCALE_WATER_NOISE = 0.3;
	
	private OpenSimplexNoise simplexNoise;
	
	public SewerDungeonGenerator(Level level, Tile sourceTile) {
		super(level, sourceTile);
	}
	
	@Override
	public boolean generate() {
		if (!super.generate()) {
			return false;
		}
		
		simplexNoise = new OpenSimplexNoise(rand.nextLong());
		
		addWaterBodies();
		
		return verify();
	}
	
	private void addWaterBodies() {
		for (int y = 0; y < level.getHeight(); y++) {
			for (int x = 0; x < level.getWidth(); x++) {
				double noise = simplexNoise.eval(x * SCALE_WATER_NOISE, y * SCALE_WATER_NOISE);
				
				if (noise > THRESHOLD_WATER_NOISE && level.getTileType(x, y) == TileType.TILE_ROOM_FLOOR) {
					level.setTileType(x, y, TileType.TILE_ROOM_SEWER_WATER);
				}
			}
		}
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
