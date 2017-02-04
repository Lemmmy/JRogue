package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;

import java.util.Arrays;
import java.util.Objects;

public class GeneratorCave extends DungeonGenerator {
	private static final float PROBABILITY_INITIAL_FLOOR = 0.6f;
	
	private static final int R1_CUTOFF = 5;
	private static final int R2_CUTOFF = 2;
	
	private static final int PASS_COUNT = 4;
	
	private Tile[] tempTiles;
	
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
		airPass();
		
		for (int i = 0; i < 6; i++) {
			pass(i == 0);
		}
		
		wallPass();
		
		return true;
	}
	
	private void initialiseTempTiles(boolean firstPass) {
		int width = level.getWidth();
		int height = level.getHeight();
		
		tempTiles = new Tile[width * height];
		
		for (int i = 0; i < width * height; i++) {
			int x = i % width;
			int y = (int) Math.floor(i / width);
			
			tempTiles[i] = new Tile(
				level,
				firstPass ? TileType.TILE_GROUND
						  : level.getTileStore().getTileType(x, y),
				x, y
			);
		}
	}
	
	private void flushTempTiles() {
		Arrays.stream(tempTiles).forEach(t -> level.getTileStore().setTileType(t.getX(), t.getY(), t.getType()));
	}
	
	private void airPass() {
		Arrays.stream(level.getTileStore().getTiles())
			.filter(this::isTileInBounds)
			.forEach(t -> {
				if (rand.nextFloat() <= PROBABILITY_INITIAL_FLOOR) {
					t.setType(TileType.TILE_CAVE_FLOOR); // TODO: cave floor tile
				}
			});
	}
	
	private void pass(boolean firstPass) {
		initialiseTempTiles(firstPass);
		
		Arrays.stream(level.getTileStore().getTiles())
			.filter(this::isTileInBounds)
			.forEach(t -> setTempTileType(
				t.getX(), t.getY(),
				tilePass(t) ? TileType.TILE_GROUND : TileType.TILE_CAVE_FLOOR
			));
		
		flushTempTiles();
	}
	
	private boolean tilePass(Tile tile) {
		int x = tile.getX(),
			y = tile.getY();
		
		int cr1 = getAdjacentCountR1(x, y),
			cr2 = getAdjacentCountR2(x, y);
		
		return cr1 >= R1_CUTOFF || cr2 <= R2_CUTOFF;
	}
	
	private void wallPass() {
		Arrays.stream(level.getTileStore().getTiles())
			.filter(t -> t.getType() == TileType.TILE_GROUND)
			.forEach(t -> {
				TileType[] adjacentTileTypes = level.getTileStore().getOctAdjacentTileTypes(t.getX(), t.getY());
				
				Arrays.stream(adjacentTileTypes)
					.filter(t2 -> t2 == TileType.TILE_CAVE_FLOOR)
					.findFirst()
					.ifPresent(__ -> t.setType(TileType.TILE_CAVE_WALL));
			});
	}
	
	public Tile getTempTile(int x, int y) {
		int width = level.getWidth();
		int height = level.getHeight();
		
		if (x < 0 || y < 0 || x >= width || y >= height) return null;
		return tempTiles[width * y + x];
	}
	
	public TileType getTempTileType(int x, int y) {
		int width = level.getWidth();
		int height = level.getHeight();
		
		if (x < 0 || y < 0 || x >= width || y >= height) return null;
		return tempTiles[width * y + x].getType();
	}
	
	public void setTempTileType(int x, int y, TileType tile) {
		int width = level.getWidth();
		int height = level.getHeight();
		
		if (tile.getID() < 0) return;
		if (x < 0 || y < 0 || x >= width || y >= height) return;
		tempTiles[width * y + x].setType(tile);
	}
	
	private int getAdjacentCountR1(int x, int y) {
		return (int) Arrays.stream(level.getTileStore().getOctAdjacentTiles(x, y))
			.filter(Objects::nonNull)
			.filter(t -> t.getType().getSolidity() == TileType.Solidity.SOLID)
			.count();
	}
	
	private int getAdjacentCountR2(int x, int y) {
		int c = 0;
		
		for (int j = y - 2; j <= y + 2; j++) {
			for (int i = x - 2; i <= x + 2; i++) {
				if (Math.abs(j - y) == 2 && Math.abs(i - x) == 2) {
					continue;
				}
				
				TileType t = level.getTileStore().getTileType(i, j);
				
				if (t == null) {
					continue;
				}
				
				if (t.getSolidity() == TileType.Solidity.SOLID) {
					c++;
				}
			}
		}
		
		return c;
	}
	
	private boolean isTileInBounds(Tile t) {
		return t.getX() > 0 &&
			   t.getY() > 0 &&
			   t.getX() < level.getWidth() - 1 &&
			   t.getY() < level.getHeight() - 1;
	}
}
