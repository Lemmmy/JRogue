package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;

import java.util.Arrays;

public class GeneratorCave extends DungeonGenerator {
	private static final float PROBABILITY_INITIAL_FLOOR = 0.6f;
	
	public GeneratorCave(Level level, Tile sourceTile) {
		super(level, sourceTile);
	}
	
	@Override
	public Climate getClimate() {
		return Climate.MID;
	}
	
	@Override
	public MonsterSpawningStrategy getMonsterSpawningStrategy() {
		return null;
	}
	
	@Override
	public boolean generate() {
		passAir();
		
		return true;
	}
	
	private void passAir() {
		Arrays.stream(level.getTileStore().getTiles())
			.filter(t -> t.getX() != 0 && t.getY() != 0 && t.getX() != level.getWidth() && t.getY() != level.getHeight())
			.forEach(t -> {
				if (rand.nextFloat() <= PROBABILITY_INITIAL_FLOOR) {
					t.setType(TileType.TILE_ROOM_FLOOR); // TODO: cave floor tile
				}
			});
	}
}
