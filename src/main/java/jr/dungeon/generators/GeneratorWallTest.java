package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.entities.decoration.EntityTorch;
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
		for (int i = 2; i < 10; i++) {
			level.tileStore.setTileType(i, 2, TileType.TILE_ROOM_WALL);
		}
		
		for (int i = 2; i < 10; i++) {
			level.tileStore.setTileType(10, i, TileType.TILE_ROOM_WALL);
		}
		
		level.tileStore.setTileType(4, 2, TileType.TILE_ROOM_DOOR_OPEN);
		level.tileStore.setTileType(6, 2, TileType.TILE_ROOM_DOOR_CLOSED);
		
		level.tileStore.setTileType(2, 4, TileType.TILE_ROOM_DOOR_OPEN);
		level.tileStore.setTileType(2, 6, TileType.TILE_ROOM_DOOR_CLOSED);
		
		QuickSpawn.spawnClass(EntityTorch.class, level, 4, 4);
		
		return true;
	}
}
