package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.entities.monsters.fish.MonsterFish;
import jr.dungeon.entities.monsters.fish.MonsterPufferfish;
import jr.dungeon.generators.rooms.Room;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.states.TileStateClimbable;
import jr.utils.OpenSimplexNoise;
import jr.utils.RandomUtils;

import java.util.Arrays;
import java.util.List;

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
		if (!super.generate()) {
			return false;
		}
		
		simplexNoise = new OpenSimplexNoise(rand.nextLong());
		
		addWaterBodies();
		spawnFish();
		
		if (spawnSewers && level.getDepth() == sewerStartDepth) {
			addSewerStart();
		}
		
		return verify();
	}
	
	@Override
	public TileType getTorchTileType() {
		return TileType.TILE_ROOM_TORCH_FIRE;
	}
	
	private void addWaterBodies() {
		for (int y = 0; y < level.getHeight(); y++) {
			for (int x = 0; x < level.getWidth(); x++) {
				double noise = simplexNoise.eval(x * scaleWaterNoise, y * scaleWaterNoise);
				
				if (noise > thresholdWaterNoise && (level.getTileStore()
					.getTileType(x, y) == TileType.TILE_GROUND || level.getTileStore().getTileType(x, y) == TileType.TILE_ROOM_FLOOR)) {
					if (level.getTileStore().getTileType(x, y) == TileType.TILE_ROOM_FLOOR && noise > thresholdWaterNoisePuddle) {
						level.getTileStore().setTileType(x, y, TileType.TILE_ROOM_PUDDLE);
					} else {
						TileType[] adjacentTiles = level.getTileStore().getAdjacentTileTypes(x, y);
						
						boolean skip = false;
						
						for (TileType tile : adjacentTiles) {
							if (tile != null && tile != TileType.TILE_GROUND && tile != TileType.TILE_GROUND_WATER) {
								skip = true;
							}
						}
						
						if (skip) {
							continue;
						}
						
						level.getTileStore().setTileType(x, y, TileType.TILE_GROUND_WATER);
					}
				}
			}
		}
	}
	
	private void spawnFish() {
		Tile[] waterTiles = Arrays.stream(level.getTileStore().getTiles())
			.filter(t -> t.getType() == TileType.TILE_GROUND_WATER)
			.toArray(Tile[]::new);
		
		if (waterTiles.length < 5) { return; }
		
		int swarmCount = jrand.nextInt(maxFishSwarms - minFishSwarms) + minFishSwarms;
		int colourCount = MonsterFish.FishColour.values().length;
		
		for (int i = 0; i < swarmCount; i++) {
			Tile swarmTile = RandomUtils.jrandomFrom(waterTiles);
			
			List<Tile> surroundingTiles = level.getTileStore()
				.getTilesInRadius(swarmTile.getX(), swarmTile.getY(), jrand.nextInt(2) + 2);
			
			if (RandomUtils.roll(4) == 1) { // spawn a swarm of pufferfish
				for (Tile tile : surroundingTiles) {
					if (tile.getType() == TileType.TILE_GROUND_WATER &&
						jrand.nextDouble() <= probabilityPufferfish &&
						level.getEntityStore().getEntitiesAt(tile.getX(), tile.getY()).size() == 0) {
						
						level.getEntityStore()
							.addEntity(new MonsterPufferfish(level.getDungeon(), level, tile.getX(), tile.getY()));
					}
				}
			} else { // regular swarm of two fish colours
				int f1 = rand.nextInt(colourCount);
				int f2 = (f1 + 1) % 6;
				
				MonsterFish.FishColour fishColour1 = MonsterFish.FishColour.values()[f1];
				MonsterFish.FishColour fishColour2 = MonsterFish.FishColour.values()[f2];
				
				for (Tile tile : surroundingTiles) {
					if (tile.getType() == TileType.TILE_GROUND_WATER &&
						jrand.nextDouble() < probabilityFish &&
						level.getEntityStore().getEntitiesAt(tile.getX(), tile.getY()).size() == 0) {
						
						MonsterFish.FishColour colour = rand.nextFloat() < 0.5f ? fishColour1 : fishColour2;
						level.getEntityStore()
							.addEntity(new MonsterFish(level.getDungeon(), level, tile.getX(), tile.getY(), colour));
					}
				}
			}
		}
	}
	
	private void addSewerStart() {
		Room room = RandomUtils.randomFrom(rooms);
		
		int ladderX = rand.nextInt(room.getWidth() - 2) + room.getX() + 1;
		int ladderY = rand.nextInt(room.getHeight() - 2) + room.getY() + 1;
		
		Tile ladderTile = level.getTileStore().getTile(ladderX, ladderY);
		ladderTile.setType(TileType.TILE_ROOM_LADDER_DOWN);
		
		if (ladderTile.getState() instanceof TileStateClimbable) {
			TileStateClimbable tsc = (TileStateClimbable) ladderTile.getState();
			tsc.setDestinationGenerator(GeneratorSewer.class);
		}
	}
}
