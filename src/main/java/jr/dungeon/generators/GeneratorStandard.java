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
import jr.utils.RandomUtils;

import java.util.Arrays;
import java.util.List;

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
		
		simplexNoise = new OpenSimplexNoise(rand.nextLong());
		
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
		for (int y = 0; y < level.getHeight(); y++) {
			for (int x = 0; x < level.getWidth(); x++) {
				double noise = simplexNoise.eval(x * scaleWaterNoise, y * scaleWaterNoise);
				
				if (noise > thresholdWaterNoise && (level.tileStore
					.getTileType(x, y) == getGroundTileType() || level.tileStore.getTileType(x, y) ==
					getFloorTileType())
				) {
					if (level.tileStore.getTileType(x, y) == getFloorTileType() && noise > thresholdWaterNoisePuddle) {
						level.tileStore.setTileType(x, y, getPuddleTileType());
					} else {
						TileType[] adjacentTiles = level.tileStore.getAdjacentTileTypes(x, y);
						
						boolean skip = false;
						
						for (TileType tile : adjacentTiles) {
							if (tile != null && tile != getGroundTileType() && tile != getGroundWaterTileType()) {
								skip = true;
							}
						}
						
						if (skip) {
							continue;
						}
						
						level.tileStore.setTileType(x, y, getGroundWaterTileType());
					}
				}
			}
		}
	}
	
	private void spawnFish() {
		Tile[] waterTiles = Arrays.stream(level.tileStore.getTiles())
			.filter(t -> t.getType() == getGroundWaterTileType())
			.toArray(Tile[]::new);
		
		if (waterTiles.length < 5) return;
		
		int swarmCount = jrand.nextInt(maxFishSwarms - minFishSwarms) + minFishSwarms;
		int colourCount = MonsterFish.FishColour.values().length;
		
		for (int i = 0; i < swarmCount; i++) {
			Tile swarmTile = RandomUtils.jrandomFrom(waterTiles);
			
			List<Tile> surroundingTiles = level.tileStore
				.getTilesInRadius(swarmTile.getX(), swarmTile.getY(), jrand.nextInt(2) + 2);
			
			if (RandomUtils.roll(4) == 1) { // spawn a swarm of pufferfish
				for (Tile tile : surroundingTiles) {
					if (tile.getType() == getGroundWaterTileType() &&
						jrand.nextDouble() <= probabilityPufferfish &&
						level.entityStore.getEntitiesAt(tile.getX(), tile.getY()).size() == 0) {
						
						level.entityStore
							.addEntity(new MonsterPufferfish(level.getDungeon(), level, tile.getX(), tile.getY()));
					}
				}
			} else { // regular swarm of two fish colours
				int f1 = rand.nextInt(colourCount);
				int f2 = (f1 + 1) % 6;
				
				MonsterFish.FishColour fishColour1 = MonsterFish.FishColour.values()[f1];
				MonsterFish.FishColour fishColour2 = MonsterFish.FishColour.values()[f2];
				
				for (Tile tile : surroundingTiles) {
					if (tile.getType() == getGroundWaterTileType() &&
						jrand.nextDouble() < probabilityFish &&
						level.entityStore.getEntitiesAt(tile.getX(), tile.getY()).size() == 0) {
						
						MonsterFish.FishColour colour = rand.nextFloat() < 0.5f ? fishColour1 : fishColour2;
						level.entityStore
							.addEntity(new MonsterFish(level.getDungeon(), level, tile.getX(), tile.getY(), colour));
					}
				}
			}
		}
	}
	
	private void addSewerStart() {
		Room room = RandomUtils.randomFrom(rooms);
		if (room == null) return;
		
		int ladderX = rand.nextInt(room.getWidth() - 2) + room.getX() + 1;
		int ladderY = rand.nextInt(room.getHeight() - 2) + room.getY() + 1;
		
		Tile ladderTile = level.tileStore.getTile(ladderX, ladderY);
		ladderTile.setType(TileType.TILE_LADDER_DOWN);
		
		if (ladderTile.getState() instanceof TileStateClimbable) {
			TileStateClimbable tsc = (TileStateClimbable) ladderTile.getState();
			tsc.setDestinationGenerator(GeneratorSewer.class);
		}
	}
}
