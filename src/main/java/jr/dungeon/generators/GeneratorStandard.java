package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.entities.monsters.fish.MonsterFish;
import jr.dungeon.entities.monsters.fish.MonsterPufferfish;
import jr.dungeon.generators.rooms.Room;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.states.TileStateClimbable;
import jr.utils.OpenSimplexNoise;
import jr.utils.Point;
import jr.utils.RandomUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static jr.dungeon.generators.BuildingUtils.buildArea;

@Registered(id="generatorStandard")
public class GeneratorStandard extends GeneratorRooms {
	protected double thresholdWaterNoise = 0.2;
	protected double thresholdWaterNoisePuddle = 0.5;
	protected double scaleWaterNoise = 0.2;
	
	protected double probabilityFish = 0.35;
	protected double probabilityPufferfish = 0.15;
	protected int minFishSwarms = 10;
	protected int maxFishSwarms = 25;
	
	protected int sewerStartDepth = -3;
	
	protected OpenSimplexNoise simplexNoise;
	
	protected boolean spawnSewers = true;
	protected boolean spawnWater = true;
	
	public GeneratorStandard(Level level, Tile sourceTile) {
		super(level, sourceTile);
	}
	
	@Override
	public Class<? extends DungeonGenerator> getNextGenerator() {
		return level.getDepth() <= -10 ? GeneratorIce.class :
			   GeneratorStandard.class;
	}
	
	@Override
	public Climate getClimate() {
		return Climate.WARM;
	}
	
	@Override
	public MonsterSpawningStrategy getMonsterSpawningStrategy() {
		return MonsterSpawningStrategy.STANDARD;
	}
	
	@Override
	public boolean generate() {
		level.setName("Dungeon");
		
		if (!super.generate()) {
			return false;
		}
		
		simplexNoise = new OpenSimplexNoise(RAND.nextLong());
		
		if (spawnWater) {
			addWaterBodies();
			spawnFish();
		}
		
		if (spawnSewers && level.getDepth() == sewerStartDepth) addSewerStart();
		
		return verify();
	}
	
	public TileType getPuddleTileType() {
		return TileType.TILE_ROOM_PUDDLE;
	}
	
	public TileType getGroundWaterTileType() {
		return TileType.TILE_GROUND_WATER;
	}
	
	private void addWaterBodies() {
		buildArea(tileStore, Point.ZERO, levelWidth, levelHeight, (t, p) -> {
			double noise = simplexNoise.eval(p.x * scaleWaterNoise, p.y * scaleWaterNoise);
			
			if (noise > thresholdWaterNoise && (t.getType() == getGroundTileType() || t.getType() == getFloorTileType())) {
				if (t.getType() == getFloorTileType() && noise > thresholdWaterNoisePuddle) {
					// put puddles in rooms
					return getPuddleTileType();
				} else {
					// skip if adjacent tile not ground or water (e.g. don't get water near rooms)
					return Arrays.stream(tileStore.getAdjacentTileTypes(p))
					   .filter(Objects::nonNull)
					   .anyMatch(a -> a != getGroundTileType() && a != getGroundWaterTileType())
						   ? null : getGroundWaterTileType();
				}
			}
			
			return null;
		});
	}
	
	private void spawnFish() {
		Tile[] waterTiles = Arrays.stream(tileStore.getTiles())
			.filter(t -> t.getType() == getGroundWaterTileType())
			.toArray(Tile[]::new);
		
		if (waterTiles.length < 5) return;
		
		int swarmCount = JRAND.nextInt(maxFishSwarms - minFishSwarms) + minFishSwarms;
		int colourCount = MonsterFish.FishColour.values().length;
		
		for (int i = 0; i < swarmCount; i++) {
			Tile swarmTile = RandomUtils.jrandomFrom(waterTiles);
			
			List<Tile> surroundingTiles = tileStore.getTilesInRadius(swarmTile.position, JRAND.nextInt(2) + 2);
			
			if (RandomUtils.roll(4) == 1) { // spawn a swarm of pufferfish
				for (Tile tile : surroundingTiles) {
					if (tile.getType() == getGroundWaterTileType() &&
						JRAND.nextDouble() <= probabilityPufferfish &&
						!level.entityStore.areEntitiesAt(tile.position)) {
						
						level.entityStore
							.addEntity(new MonsterPufferfish(level.getDungeon(), level, tile.position));
					}
				}
			} else { // regular swarm of two fish colours
				int f1 = RAND.nextInt(colourCount);
				int f2 = (f1 + 1) % 6;
				
				MonsterFish.FishColour fishColour1 = MonsterFish.FishColour.values()[f1];
				MonsterFish.FishColour fishColour2 = MonsterFish.FishColour.values()[f2];
				
				for (Tile tile : surroundingTiles) {
					if (tile.getType() == getGroundWaterTileType() &&
						JRAND.nextDouble() < probabilityFish &&
						!level.entityStore.areEntitiesAt(tile.position)) {
						
						MonsterFish.FishColour colour = RAND.nextFloat() < 0.5f ? fishColour1 : fishColour2;
						level.entityStore
							.addEntity(new MonsterFish(level.getDungeon(), level, tile.position, colour));
					}
				}
			}
		}
	}
	
	private void addSewerStart() {
		Room room = RandomUtils.randomFrom(rooms);
		if (room == null) return;
		
		Tile ladderTile = tileStore.setTileType(room.randomPoint(), TileType.TILE_LADDER_DOWN);
		
		if (ladderTile.getState() instanceof TileStateClimbable) {
			TileStateClimbable tsc = (TileStateClimbable) ladderTile.getState();
			tsc.setDestinationGenerator(GeneratorSewer.class);
		}
	}
}
