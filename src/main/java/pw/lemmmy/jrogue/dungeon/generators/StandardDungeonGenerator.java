package pw.lemmmy.jrogue.dungeon.generators;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.monsters.MonsterFish;
import pw.lemmmy.jrogue.dungeon.entities.monsters.MonsterPufferfish;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.OpenSimplexNoise;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StandardDungeonGenerator extends RoomGenerator {
	private static final double THRESHOLD_WATER_NOISE = 0.2;
	private static final double THRESHOLD_WATER_NOISE_PUDDLE = 0.5;
	private static final double SCALE_WATER_NOISE = 0.2;
	
	private static final double PROBABILITY_FISH = 0.35;
	private static final double PROBABILITY_PUFFERFISH = 0.15;
	private static final int MIN_FISH_SWARMS = 10;
	private static final int MAX_FISH_SWARMS = 25;
	
	private OpenSimplexNoise simplexNoise;
	
	public StandardDungeonGenerator(Level level, Tile sourceTile) {
		super(level, sourceTile);
	}
	
	@Override
	public boolean generate() {
		if (!super.generate()) {
			return false;
		}
		
		simplexNoise = new OpenSimplexNoise(rand.nextLong());
		
		addWaterBodies();
		spawnFish();
		
		return verify();
	}
	
	@Override
	public TileType getTorchTileType() {
		return TileType.TILE_ROOM_TORCH_FIRE;
	}
	
	private void addWaterBodies() {
		for (int y = 0; y < level.getHeight(); y++) {
			for (int x = 0; x < level.getWidth(); x++) {
				double noise = simplexNoise.eval(x * SCALE_WATER_NOISE, y * SCALE_WATER_NOISE);
				
				if (noise > THRESHOLD_WATER_NOISE && (level.getTileType(x, y) == TileType.TILE_GROUND || level
					.getTileType(x, y) == TileType.TILE_ROOM_FLOOR)) {
					if (level.getTileType(x, y) == TileType.TILE_ROOM_FLOOR && noise > THRESHOLD_WATER_NOISE_PUDDLE) {
						level.setTileType(x, y, TileType.TILE_ROOM_PUDDLE);
					} else {
						TileType[] adjacentTiles = level.getAdjacentTileTypes(x, y);
						
						boolean skip = false;
						
						for (TileType tile : adjacentTiles) {
							if (tile != null && tile != TileType.TILE_GROUND && tile != TileType.TILE_GROUND_WATER) {
								skip = true;
							}
						}
						
						if (skip) {
							continue;
						}
						
						level.setTileType(x, y, TileType.TILE_GROUND_WATER);
					}
				}
			}
		}
	}
	
	private void spawnFish() {
		Tile[] waterTiles = Arrays.stream(level.getTiles())
			.filter(t -> t.getType() == TileType.TILE_GROUND_WATER)
			.toArray(Tile[]::new);
		
		if (waterTiles.length < 5) { return; }
		
		int swarmCount = jrand.nextInt(MAX_FISH_SWARMS - MIN_FISH_SWARMS) + MIN_FISH_SWARMS;
		int colourCount = MonsterFish.FishColour.values().length;
		
		for (int i = 0; i < swarmCount; i++) {
			Tile swarmTile = Utils.jrandomFrom(waterTiles);
			
			List<Tile> surroundingTiles = level
				.getTilesInRadius(swarmTile.getX(), swarmTile.getY(), jrand.nextInt(2) + 2);
			
			if (Utils.roll(4) == 1) { // spawn a swarm of pufferfish
				for (Tile tile : surroundingTiles) {
					if (tile.getType() == TileType.TILE_GROUND_WATER &&
						jrand.nextDouble() <= PROBABILITY_PUFFERFISH &&
						level.getEntitiesAt(tile.getX(), tile.getY()).size() == 0) {
						
						level.addEntity(new MonsterPufferfish(level.getDungeon(), level, tile.getX(), tile.getY()));
					}
				}
			} else { // regular swarm of two fish colours
				int f1 = rand.nextInt(colourCount);
				int f2 = (f1 + 1) % 6;
				
				MonsterFish.FishColour fishColour1 = MonsterFish.FishColour.values()[f1];
				MonsterFish.FishColour fishColour2 = MonsterFish.FishColour.values()[f2];
				
				for (Tile tile : surroundingTiles) {
					if (tile.getType() == TileType.TILE_GROUND_WATER &&
						jrand.nextDouble() < PROBABILITY_FISH &&
						level.getEntitiesAt(tile.getX(), tile.getY()).size() == 0) {
						
						MonsterFish.FishColour colour = rand.nextFloat() < 0.5f ? fishColour1 : fishColour2;
						level.addEntity(new MonsterFish(level.getDungeon(), level, tile.getX(), tile.getY(), colour));
					}
				}
			}
		}
	}
}
