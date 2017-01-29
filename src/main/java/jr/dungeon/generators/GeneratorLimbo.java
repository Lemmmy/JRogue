package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;

public class GeneratorLimbo extends GeneratorStandard {
	public GeneratorLimbo(Level level, Tile sourceTile) {
		super(level, sourceTile);
		spawnWater = false;
		spawnSewers = false;
	}
	
	@Override
	public Climate getClimate() {
		return Climate.LIMBO;
	}
	
	@Override
	public MonsterSpawningStrategy getMonsterSpawningStrategy() {
		return MonsterSpawningStrategy.NONE;
	}
	
	@Override
	public TileType getTorchTileType() {
		return null;
	}
	
	@Override
	public TileType getGroundTileType() {
		return TileType.TILE_L_NOISE;
	}
	
}
