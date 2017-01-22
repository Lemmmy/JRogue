package jr.dungeon.entities.monsters.ai;

import com.github.alexeyr.pcg.Pcg32;
import jr.dungeon.tiles.Tile;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.tiles.TileType;
import jr.utils.RandomUtils;

import java.util.Arrays;

public class FishAI extends AI {
	private static final int SLEEP_DISTANCE = 20;
	private Pcg32 random = new Pcg32();
	private float moveProbability = 0.1f;
	
	public FishAI(Monster monster) {
		super(monster);
	}
	
	public void setMoveProbability(float moveProbability) {
		this.moveProbability = moveProbability;
	}
	
	@Override
	public void update() {
		if (distanceFromPlayer() >= SLEEP_DISTANCE) {
			return; // no need to move if we're far away from the player
		}
		
		if (random.nextFloat() < moveProbability) {
			Tile[] tiles = getMonster().getLevel().getAdjacentTiles(getMonster().getX(), getMonster().getY());
			Tile[] waterTiles = Arrays.stream(tiles).filter(t -> t != null && t.getType() != null && t
				.getType() == TileType.TILE_GROUND_WATER).toArray(Tile[]::new);
			
			if (waterTiles.length > 0) {
				Tile destination = RandomUtils.randomFrom(waterTiles);
				
				moveTowards(destination.getX(), destination.getY());
			}
		}
	}
}
