package jr.dungeon.generators;

import jr.JRogue;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.entities.decoration.EntityCaveCrystal;
import jr.dungeon.entities.decoration.EntityCaveCrystalSmall;
import jr.dungeon.entities.decoration.EntityStalagmites;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Solidity;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.states.TileStateClimbable;
import jr.utils.Distance;
import jr.utils.Path;
import jr.utils.Point;
import jr.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Registered(id="generatorCave")
public class GeneratorCave extends DungeonGenerator {
	private static final float PROBABILITY_INITIAL_FLOOR = 0.4f;
	private static final float PROBABILITY_STALAGMITES = 0.25f;
	private static final float PROBABILITY_SMALL_CRYSTALS = 0.04f;
	private static final float PROBABILITY_CRYSTAL = 0.01f;
	
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
		return MonsterSpawningStrategy.CAVE;
	}
	
	@Override
	public boolean generate() {
		level.setName("Caves");
		
		airPass();
		
		for (int i = 0; i < 6; i++) {
			pass(i == 0);
		}
		
		wallPass();
		
		chooseSpawn();
		chooseExit();
		
		stalagmitePass();
		crystalPass();
		
		return verify();
	}
	
	private void initialiseTempTiles(boolean firstPass) {
		tempTiles = new Tile[levelWidth * levelHeight];
		
		for (int i = 0; i < levelWidth * levelHeight; i++) {
			int x = i % levelWidth;
			int y = (int) Math.floor(i / levelWidth);
			
			tempTiles[i] = new Tile(
				level,
				firstPass ? TileType.TILE_GROUND
						  : tileStore.getTileType(Point.get(x, y)),
				x, y
			);
		}
	}
	
	private void flushTempTiles() {
		Arrays.stream(tempTiles).forEach(t -> tileStore.setTileType(t.position, t.getType()));
	}
	
	private void airPass() {
		Arrays.stream(tileStore.getTiles())
			.filter(this::notByEdge)
			.forEach(t -> {
				if (RAND.nextFloat() <= PROBABILITY_INITIAL_FLOOR) {
					t.setType(TileType.TILE_CAVE_FLOOR); // TODO: cave floor tile
				}
			});
	}
	
	private void pass(boolean firstPass) {
		initialiseTempTiles(firstPass);
		
		Arrays.stream(tileStore.getTiles())
			.filter(this::notByEdge)
			.forEach(t -> setTempTileType(
				t.position,
				tilePass(t) ? TileType.TILE_GROUND : TileType.TILE_CAVE_FLOOR
			));
		
		flushTempTiles();
	}
	
	private boolean tilePass(Tile tile) {
		int cr1 = getAdjacentCountR1(tile.position),
			cr2 = getAdjacentCountR2(tile.position);
		
		return cr1 >= R1_CUTOFF || cr2 <= R2_CUTOFF;
	}
	
	private void wallPass() {
		Arrays.stream(tileStore.getTiles())
			.filter(t -> t.getType() == TileType.TILE_GROUND)
			.forEach(t -> {
				TileType[] adjacentTileTypes = tileStore.getOctAdjacentTileTypes(t.position);
				
				Arrays.stream(adjacentTileTypes)
					.filter(t2 -> t2 == TileType.TILE_CAVE_FLOOR)
					.findFirst()
					.ifPresent(__ -> t.setType(TileType.TILE_CAVE_WALL));
			});
	}
	
	private void chooseSpawn() {
		List<Tile> validSpawnTiles = Arrays.stream(tileStore.getTiles())
			.filter(t -> t.getType().isFloor())
			.filter(t -> Arrays.stream(tileStore.getAdjacentTileTypes(t.position))
				.filter(TileType::isFloor)
				.count() == 4)
			.collect(Collectors.toList());
		
		spawnTile = RandomUtils.randomFrom(validSpawnTiles);
		assert spawnTile != null;
		
		if (sourceTile != null) {
			spawnTile.setType(TileType.TILE_LADDER_UP);
			
			if (sourceTile.getLevel().getDepth() < level.getDepth()) {
				spawnTile.setType(TileType.TILE_LADDER_DOWN);
			}
			
			if (spawnTile.getState() instanceof TileStateClimbable) {
				TileStateClimbable tsc = (TileStateClimbable) spawnTile.getState();
				tsc.setLinkedLevelUUID(sourceTile.getLevel().getUUID());
				tsc.setDestinationPosition(sourceTile.position);
			}
		}
		
		level.setSpawnPoint(spawnTile.position);
	}
	
	private void chooseExit() {
		List<Tile> validExitTiles = Arrays.stream(tileStore.getTiles())
			.filter(t -> t.getType().isFloor())
			.filter(t -> Arrays.stream(tileStore.getAdjacentTileTypes(t.position))
							.filter(TileType::isFloor)
							.count() == 4)
			.filter(t -> Distance.chebyshev(t.position, spawnTile.position) > DISTANCE_SPAWN_EXIT)
			.collect(Collectors.toList());
		
		exitTile = RandomUtils.randomFrom(validExitTiles);
		assert exitTile != null;
		
		tileStore.setTileType(exitTile.position, TileType.TILE_LADDER_DOWN);
		
		if (sourceTile != null && sourceTile.getLevel().getDepth() < level.getDepth()) {
			tileStore.setTileType(exitTile.position, TileType.TILE_LADDER_UP);
		}
		
		exitTile = tileStore.getTile(exitTile.position);
		
		if (exitTile.getState() instanceof TileStateClimbable) {
			TileStateClimbable tsc = (TileStateClimbable) exitTile.getState();
			tsc.setDestinationGenerator(getNextGenerator());
		}
	}
	
	private Stream<Tile> floorAdjacentToWall() {
		return Arrays.stream(level.tileStore.getTiles())
			.filter(t -> t.getType() == TileType.TILE_CAVE_FLOOR)
			.filter(t -> Arrays.stream(level.tileStore.getAdjacentTileTypes(t.position))
							   .anyMatch(t2 -> t2 == TileType.TILE_CAVE_WALL))
			.filter(t -> !level.entityStore.getEntitiesAt(t.position).findAny().isPresent());
	}
	
	private void stalagmitePass() {
		floorAdjacentToWall().forEach(t -> {
			if (RandomUtils.randomFloat() < PROBABILITY_STALAGMITES) {
				QuickSpawn.spawnClass(EntityStalagmites.class, level, t.position);
			}
		});
	}
	
	private void crystalPass() {
		floorAdjacentToWall().forEach(t -> {
			final Class<? extends Entity> crystalType;
			
			if (RandomUtils.randomFloat() < PROBABILITY_SMALL_CRYSTALS) {
				crystalType = EntityCaveCrystalSmall.class;
			} else if (RandomUtils.randomFloat() < PROBABILITY_CRYSTAL) {
				crystalType = EntityCaveCrystal.class;
			} else {
				return;
			}
			
			QuickSpawn.spawnClass(crystalType, level, t.position);
		});
	}
	
	public Tile getTempTile(int x, int y) {
		int width = levelWidth;
		int height = levelHeight;
		
		if (x < 0 || y < 0 || x >= width || y >= height) return null;
		return tempTiles[y * width + x];
	}

	public Tile getTempTile(Point point) {
		return getTempTile(point.x, point.y);
	}
	
	public TileType getTempTileType(int x, int y) {
		return getTempTile(x, y).getType();
	}

	public TileType getTempTileType(Point point) {
		return getTempTileType(point.x, point.y);
	}
	
	public void setTempTileType(int x, int y, TileType tile) {
		int width = levelWidth;
		int height = levelHeight;
		
		if (tile.getID() < 0) return;
		if (x < 0 || y < 0 || x >= width || y >= height) return;
		tempTiles[y * width + x].setType(tile);
	}

	public void setTempTileType(Point point, TileType tile) {
		setTempTileType(point.x, point.y, tile);
	}
	
	private int getAdjacentCountR1(Point p) {
		return (int) Arrays.stream(tileStore.getOctAdjacentTiles(p))
			.filter(Objects::nonNull)
			.filter(t -> t.getType().getSolidity() == Solidity.SOLID)
			.count();
	}
	
	private int getAdjacentCountR2(Point p) {
		int c = 0;
		
		for (int j = p.y - 2; j <= p.y + 2; j++) {
			for (int i = p.x - 2; i <= p.x + 2; i++) {
				if (Math.abs(j - p.y) == 2 && Math.abs(i - p.x) == 2) {
					continue;
				}
				
				TileType t = tileStore.getTileType(Point.get(i, j));
				
				if (t != null && t.getSolidity() == Solidity.SOLID) {
					c++;
				}
			}
		}
		
		return c;
	}
	
	private boolean notByEdge(Tile t) {
		Point p = t.position;
		return p.x > 0 && p.y > 0 && p.x < levelWidth - 1 && p.y < levelHeight - 1;
	}
	
	public boolean verify() {
		Path path = pathfinder.findPath(
			level,
			spawnTile.position,
			exitTile.position,
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
