package jr.dungeon.generators;

import jr.JRogue;
import jr.dungeon.Level;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.states.TileStateClimbable;
import jr.utils.Path;
import jr.utils.Point;
import jr.utils.RandomUtils;
import jr.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GeneratorCave extends DungeonGenerator {
	private static final float PROBABILITY_INITIAL_FLOOR = 0.4f;
	
	private static final int R1_CUTOFF = 5;
	private static final int R2_CUTOFF = 2;
	
	private static final int PASS_COUNT = 4;
	
	private static final int DISTANCE_SPAWN_EXIT = 30;
	
	private Tile[] tempTiles;
	private Tile spawnTile, exitTile;
	
	private VerificationPathfinder pathfinder = new VerificationPathfinder();
	
	public GeneratorCave(Level level, Tile sourceTile) {
		super(level, sourceTile);
	}
	
	public Class<? extends DungeonGenerator> getNextGenerator() {
		return GeneratorCave.class;
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
		
		chooseSpawn();
		chooseExit();
		
		return verify();
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
	
	private void chooseSpawn() {
		List<Tile> validSpawnTiles = Arrays.stream(level.getTileStore().getTiles())
			.filter(t -> t.getType().isFloor())
			.filter(t -> Arrays.stream(level.getTileStore().getAdjacentTileTypes(t.getX(), t.getY()))
				.filter(TileType::isFloor)
				.count() == 4)
			.collect(Collectors.toList());
		
		spawnTile = RandomUtils.randomFrom(validSpawnTiles);
		assert spawnTile != null;
		
		int spawnX = spawnTile.getX();
		int spawnY = spawnTile.getY();
		
		if (sourceTile != null) {
			spawnTile.setType(TileType.TILE_LADDER_UP);
			
			if (sourceTile.getLevel().getDepth() < level.getDepth()) {
				spawnTile.setType(TileType.TILE_LADDER_DOWN);
			}
			
			if (spawnTile.getState() instanceof TileStateClimbable) {
				TileStateClimbable tsc = (TileStateClimbable) spawnTile.getState();
				tsc.setLinkedLevelUUID(sourceTile.getLevel().getUUID());
				tsc.setDestinationPosition(sourceTile.getX(), sourceTile.getY());
			}
		}
		
		level.setSpawnPoint(spawnX, spawnY);
	}
	
	private void chooseExit() {
		List<Tile> validExitTiles = Arrays.stream(level.getTileStore().getTiles())
			.filter(t -> t.getType().isFloor())
			.filter(t -> Arrays.stream(level.getTileStore().getAdjacentTileTypes(t.getX(), t.getY()))
							.filter(TileType::isFloor)
							.count() == 4)
			.filter(t -> Utils.chebyshevDistance(t.getX(), t.getY(), spawnTile.getX(), spawnTile.getY()) > DISTANCE_SPAWN_EXIT)
			.collect(Collectors.toList());
		
		exitTile = RandomUtils.randomFrom(validExitTiles);
		assert exitTile != null;
		
		int exitX = exitTile.getX();
		int exitY = exitTile.getY();
		
		level.getTileStore().setTileType(exitX, exitY, TileType.TILE_LADDER_DOWN);
		
		if (sourceTile != null && sourceTile.getLevel().getDepth() < level.getDepth()) {
			level.getTileStore().setTileType(exitX, exitY, TileType.TILE_LADDER_UP);
		}
		
		exitTile = level.getTileStore().getTile(exitX, exitY);
		
		if (exitTile.getState() instanceof TileStateClimbable) {
			TileStateClimbable tsc = (TileStateClimbable) exitTile.getState();
			tsc.setDestinationGenerator(getNextGenerator());
		}
	}
	
	public Tile getTempTile(int x, int y) {
		int width = level.getWidth();
		int height = level.getHeight();
		
		if (x < 0 || y < 0 || x >= width || y >= height) return null;
		return tempTiles[width * y + x];
	}

	public Tile getTempTile(Point p) {
		return getTempTile(p.getX(), p.getY());
	}
	
	public TileType getTempTileType(int x, int y) {
		return getTempTile(x, y).getType();
	}

	public TileType getTempTileType(Point p) {
		return getTempTileType(p.getX(), p.getY());
	}
	
	public void setTempTileType(int x, int y, TileType tile) {
		int width = level.getWidth();
		int height = level.getHeight();
		
		if (tile.getID() < 0) return;
		if (x < 0 || y < 0 || x >= width || y >= height) return;
		tempTiles[width * y + x].setType(tile);
	}

	public void setTempTileType(Point p, TileType tile) {
		setTempTileType(p.getX(), p.getY(), tile);
	}
	
	private int getAdjacentCountR1(int x, int y) {
		return (int) Arrays.stream(level.getTileStore().getOctAdjacentTiles(x, y))
			.filter(Objects::nonNull)
			.filter(t -> t.getType().getSolidity() == TileType.Solidity.SOLID)
			.count();
	}

	private int getAdjacentCountR1(Point p) {
		return getAdjacentCountR1(p.getX(), p.getY());
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

	private int getAdjacentCountR2(Point p) {
		return getAdjacentCountR2(p.getX(), p.getY());
	}
	
	private boolean isTileInBounds(Tile t) {
		return t.getX() > 0 &&
			   t.getY() > 0 &&
			   t.getX() < level.getWidth() - 1 &&
			   t.getY() < level.getHeight() - 1;
	}
	
	public boolean verify() {
		Path path = pathfinder.findPath(
			level,
			spawnTile.getX(),
			spawnTile.getY(),
			exitTile.getX(),
			exitTile.getY(),
			Integer.MAX_VALUE,
			true,
			new ArrayList<>()
		);
		
		if (path == null) {
			JRogue.getLogger().debug("Level was generated unreachable - regenerating");
		}
		
		return path != null;
	}
}
