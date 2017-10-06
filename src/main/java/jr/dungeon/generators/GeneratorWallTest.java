package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;

public class GeneratorWallTest extends DungeonGenerator {
	/**
	 * @param level      The {@link Level} that this generator is generating for.
	 * @param sourceTile The tile that the Player enters this level via, typically the staircase down in the previous
	 */
	public GeneratorWallTest(Level level, Tile sourceTile) {
		super(level, sourceTile);
	}
	
	@Override
	public Climate getClimate() {
		return Climate.WARM;
	}
	
	@Override
	public MonsterSpawningStrategy getMonsterSpawningStrategy() {
		return MonsterSpawningStrategy.NONE;
	}
	
	@Override
	public boolean generate() {
		level.tileStore.setTileType(0, 0, TileType.TILE_ROOM_WALL);
		level.tileStore.setTileType(1, 0, TileType.TILE_ROOM_WALL);
		level.tileStore.setTileType(2, 0, TileType.TILE_ROOM_WALL);
		level.tileStore.setTileType(2, 1, TileType.TILE_ROOM_WALL);
		level.tileStore.setTileType(2, 2, TileType.TILE_ROOM_WALL);
		
		return true;
	}
}
