package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;

public class GeneratorLimbo extends DungeonGenerator {
	public GeneratorLimbo(Level level, Tile sourceTile) {
		super(level, sourceTile);
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
	public boolean generate() {
		for (int y = 0; y < level.getHeight(); ++y) {
			for (int x = 0; x < level.getWidth(); ++x) {
				level.getTileStore().setTileType(x, y, TileType.TILE_L_NOISE);
			}
		}

		return true;
	}
}
