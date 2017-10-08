package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.generators.rooms.features.FeatureSewerDrain;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.OpenSimplexNoise;

public class GeneratorSewer extends GeneratorRooms {
	private static final double THRESHOLD_WATER_NOISE = 0.1;
	private static final double SCALE_WATER_NOISE = 0.3;
	
	private OpenSimplexNoise simplexNoise;
	
	{
		probabilitySpecialFeatureCount.clear();
		
		probabilitySpecialFeatureCount.add(1, 1);
		probabilitySpecialFeatureCount.add(2, 2);
		probabilitySpecialFeatureCount.add(4, 3);
		probabilitySpecialFeatureCount.add(5, 4);
	}
	
	{
		probabilitySpecialFeatures.clear();
		
		probabilitySpecialFeatures.add(1, FeatureSewerDrain.class);
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
		level.setName("Sewers");
		
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
	public boolean shouldAddTorches() {
		return true;
	}
	
	@Override
	public TileType getDownstairsTileType() {
		return TileType.TILE_LADDER_DOWN;
	}
	
	@Override
	public TileType getUpstairsTileType() {
		return TileType.TILE_LADDER_UP;
	}
}
