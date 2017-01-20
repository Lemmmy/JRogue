package pw.lemmmy.jrogue.dungeon.generators;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.generators.rooms.features.FeatureSewerDrain;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.OpenSimplexNoise;

public class GeneratorSewer extends GeneratorRooms {
	private static final double THRESHOLD_WATER_NOISE = 0.1;
	private static final double SCALE_WATER_NOISE = 0.3;
	
	private OpenSimplexNoise simplexNoise;
	
	static {
		PROBABILITY_SPECIAL_FEATURES.clear();
		
		PROBABILITY_SPECIAL_FEATURES.add(1, FeatureSewerDrain.class);
	}
	
	public GeneratorSewer(Level level, Tile sourceTile) {
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
					level.setTileType(x, y, TileType.TILE_SEWER_WATER);
				}
			}
		}
	}
	
	@Override
	public Climate getClimate() {
		return Climate.MID;
	}
	
	@Override
	public TileType getWallTileType() {
		return TileType.TILE_SEWER_WALL;
	}
	
	@Override
	public TileType getTorchTileType() {
		return TileType.TILE_SEWER_WALL;
	}
}
