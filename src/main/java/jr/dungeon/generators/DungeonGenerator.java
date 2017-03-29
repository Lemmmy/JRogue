package jr.dungeon.generators;

import com.github.alexeyr.pcg.Pcg32;
import jr.dungeon.Level;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import lombok.Getter;

import java.util.Random;

/**
 * Generic dungeon level generator class with many utilities for generation.
 */
public abstract class DungeonGenerator {
	/**
	 * The {@link Level} that this generator is generating for.
	 */
	@Getter protected Level level;
	/**
	 * The tile that the Player enters this level via, typically the staircase down in the previous level. Usually
	 * used for assigning generator types.
	 */
	@Getter protected Tile sourceTile;
	
	/**
	 * Local random number generator instance - uses pcg32 algo.
	 */
	protected Pcg32 rand = new Pcg32();
	/**
	 * Local random number generator instance - uses java algo.
	 */
	protected Random jrand = new Random();
	
	/**
	 * @param level The {@link Level} that this generator is generating for.
	 * @param sourceTile The tile that the Player enters this level via, typically the staircase down in the previous
	 *                   level. Usually used for assigning generator types.
	 */
	public DungeonGenerator(Level level, Tile sourceTile) {
		this.level = level;
		this.sourceTile = sourceTile;
	}
	
	public abstract Climate getClimate();
	
	public abstract MonsterSpawningStrategy getMonsterSpawningStrategy();
	
	/**
	 * Generates the level.
	 *
 	 * @return Whether the level is valid and complete or not. If false, a new level will be generated.
	 */
	public abstract boolean generate();
	
	/**
	 * Quick utility method for generating a random number within a range. Uses the #rand instance (pcg32 algo).
	 *
	 * @param min The minimum bound for the random number (inclusive).
	 * @param max The maximum bound for the random number (exclusive).
	 *
	 * @return A (hopefully) random number within the min/max bounds.
	 */
	protected int nextInt(int min, int max) {
		return rand.nextInt(max - min) + min;
	}
	
	/**
	 * Places a line of tiles.
	 *
	 * @param startX The starting X position of the line.
	 * @param startY The starting Y position of the line.
	 * @param endX The ending X position of the line.
	 * @param endY The ending Y position of the line.
	 * @param tile The tile to build the line with.
	 */
	protected void buildLine(int startX,
							 int startY,
							 int endX,
							 int endY,
							 TileType tile) {
		float diffX = endX - startX;
		float diffY = endY - startY;
		
		float dist = Math.abs(diffX) + Math.abs(diffY);
		
		float dx = diffX / dist;
		float dy = diffY / dist;
		
		for (int i = 0; i <= Math.ceil(dist); i++) {
			int x = Math.round(startX + dx * i);
			int y = Math.round(startY + dy * i);
			
			if (level.getTileStore().getTileType(x, y).isBuildable()) {
				level.getTileStore().setTileType(x, y, tile);
			}
		}
	}
}
