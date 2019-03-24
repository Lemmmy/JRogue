package jr.dungeon.entities.monsters.ai;

import com.github.alexeyr.pcg.Pcg32;
import com.google.gson.annotations.Expose;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Registered(id="aiFish")
public class FishAI extends AI {
	private static final int SLEEP_DISTANCE = 20;
	
	private static final Pcg32 RAND = new Pcg32();
	
	@Expose @Getter @Setter private float moveProbability = 0.1f;
	
	public FishAI(Monster monster) {
		super(monster);
	}
	
	@Override
	public void update() {
		if (distanceFromPlayer() >= SLEEP_DISTANCE) {
			return; // no need to move if we're far away from the player
		}
		
		if (RAND.nextFloat() < moveProbability) {
			Tile[] tiles = getMonster().getLevel().tileStore
				.getAdjacentTiles(getMonster().getX(), getMonster().getY());
			Tile[] waterTiles = Arrays.stream(tiles).filter(t -> t != null && t.getType() != null && t
				.getType() == TileType.TILE_GROUND_WATER).toArray(Tile[]::new);
			
			if (waterTiles.length > 0) {
				Tile destination = RandomUtils.randomFrom(waterTiles);
				
				moveTowards(destination.getX(), destination.getY());
			}
		}
	}
}
