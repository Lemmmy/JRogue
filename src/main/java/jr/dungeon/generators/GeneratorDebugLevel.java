package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.TileStore;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.entities.decoration.EntityCandlestick;
import jr.dungeon.entities.decoration.EntityTorch;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.RandomUtils;

public class GeneratorDebugLevel extends DungeonGenerator {
	/**
	 * @param level      The {@link Level} that this generator is generating for.
	 * @param sourceTile The tile that the Player enters this level via, typically the staircase down in the previous
	 */
	public GeneratorDebugLevel(Level level, Tile sourceTile) {
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
		TileStore ts = getLevel().tileStore;
		
		int startX = 3;
		int startY = 3;
		int width = 9;
		int height = 7;
		
		for (int y = startY; y < startY + height; y++) {
			for (int x = startX; x < startX + width; x++) {
				boolean wall = x == startX || x == startX + width - 1 ||
					y == startY || y == startY + height - 1;
				
				if (wall) {
					ts.setTileType(x, y, TileType.TILE_ROOM_WALL);
				} else {
					ts.setTileType(x, y, TileType.TILE_ROOM_FLOOR);
				}
			}
		}
		
		ts.setTileType(startX, startY + 3, TileType.TILE_ROOM_DOOR_CLOSED);
		
		level.setSpawnPoint(startX + 4, startY + 2);
		QuickSpawn.spawnClass(EntityCandlestick.class, level, startX + 3, startY + 2);
		
		QuickSpawn.spawnClass(EntityTorch.class, level, startX + 2, startY + 2);
		QuickSpawn.spawnClass(EntityTorch.class, level, startX + 5, startY + 2);
		QuickSpawn.spawnClass(EntityTorch.class, level, startX + 2, startY + 4);
		QuickSpawn.spawnClass(EntityTorch.class, level, startX + 5, startY + 4);
		
		return true;
	}
}
