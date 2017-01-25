package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.tiles.Tile;
import jr.utils.OpenSimplexNoise;
import jr.dungeon.generators.rooms.features.FeatureSewerDrain;
import jr.dungeon.tiles.TileType;

public class GeneratorSewer extends GeneratorRooms {
	private static final double THRESHOLD_WATER_NOISE = 0.1;
	private static final double SCALE_WATER_NOISE = 0.3;
	
	private OpenSimplexNoise simplexNoise;
	
	static {
		PROBABILITY_SPECIAL_FEATURE_COUNT.clear();
		
		PROBABILITY_SPECIAL_FEATURE_COUNT.add(1, 1);
		PROBABILITY_SPECIAL_FEATURE_COUNT.add(2, 2);
		PROBABILITY_SPECIAL_FEATURE_COUNT.add(4, 3);
		PROBABILITY_SPECIAL_FEATURE_COUNT.add(5, 4);
	}
	
	static {
		PROBABILITY_SPECIAL_FEATURES.clear();
		
		PROBABILITY_SPECIAL_FEATURES.add(1, FeatureSewerDrain.class);
	}
	
	public GeneratorSewer(Level level, Tile sourceTile) {
		super(level, sourceTile);
	}
	
	@Override
	public Class<? extends DungeonGenerator> getNextGenerator() {
		return level.getDepth() <= -7 ? GeneratorStandard.class :
			   							GeneratorSewer.class;
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
				
				if (noise > THRESHOLD_WATER_NOISE && level.tileStore.getTileType(x, y) == TileType.TILE_ROOM_FLOOR) {
					level.tileStore.setTileType(x, y, TileType.TILE_SEWER_WATER);
				}
			}
		}
	}
	
	@Override
	public Climate getClimate() {
		return Climate.MID;
	}
	
	@Override
	public MonsterSpawningStrategy getMonsterSpawningStrategy() {
		return MonsterSpawningStrategy.SEWER;
	}
	
	@Override
	public TileType getWallTileType() {
		return TileType.TILE_SEWER_WALL;
	}
	
	@Override
	public TileType getTorchTileType() {
		return TileType.TILE_SEWER_WALL;
	}
	
	@Override
	public TileType getDownstairsTileType() {
		return TileType.TILE_ROOM_LADDER_DOWN;
	}
	
	@Override
	public TileType getUpstairsTileType() {
		return TileType.TILE_ROOM_LADDER_UP;
	}
}
