package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;

import java.util.Arrays;
import java.util.Objects;

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
		pass();
		
		return true;
	}
	
	private void passAir() {
		Arrays.stream(level.getTileStore().getTiles())
			.filter(this::isTileInBounds)
			.forEach(t -> {
				if (rand.nextFloat() <= PROBABILITY_INITIAL_FLOOR) {
					t.setType(TileType.TILE_ROOM_FLOOR); // TODO: cave floor tile
				}
			});
	}
	
	private void pass() {
		Arrays.stream(level.getTileStore().getTiles())
			.filter(this::isTileInBounds)
			.forEach(t -> {
				int pass = tilePass(t);
				
				t.setType(pass == 1 ? TileType.TILE_GROUND : TileType.TILE_ROOM_FLOOR);
			});
	}
	
	private int tilePass(Tile tile) {
		int x = tile.getX();
		int y = tile.getY();
		
		int adjacentCount = getAdjacentCount(x, y);
		
		if (tile.getType().getSolidity() == TileType.Solidity.SOLID) {
			if (adjacentCount >= 4) {
				return 1;
			}
			
			if (adjacentCount < 2) {
				return 0;
			}
		} else {
			if (adjacentCount >= 5) {
				return 1;
			}
		}
		
		return 0;
	}
	
	private int getAdjacentCount(int x, int y) {
		return (int) Arrays.stream(level.getTileStore().getOctAdjacentTiles(x, y))
			.filter(Objects::nonNull)
			.filter(t -> t.getType().getSolidity() == TileType.Solidity.SOLID)
			.count();
	}
	
	private boolean isTileInBounds(Tile t) {
		return t.getX() > 0 &&
			   t.getY() > 0 &&
			   t.getX() < level.getWidth() - 1 &&
			   t.getY() < level.getHeight() - 1;
	}
}
