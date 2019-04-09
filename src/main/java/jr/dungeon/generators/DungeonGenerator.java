package jr.dungeon.generators;

import com.github.alexeyr.pcg.Pcg32;
import jr.dungeon.Level;
import jr.dungeon.TileStore;
import jr.dungeon.serialisation.HasRegistry;
import jr.dungeon.tiles.Tile;
import jr.utils.RandomUtils;
import lombok.Getter;

import java.util.Random;

/**
 * Generic dungeon level generator class with many utilities for generation.
 */
@HasRegistry
public abstract class DungeonGenerator {
	/**
	 * The {@link Level} that this generator is generating for.
	 */
	@Getter protected final Level level;
	
	protected final TileStore tileStore;
	protected final int levelWidth, levelHeight;
	
	/**
	 * The tile that the Player enters this level via, typically the staircase down in the previous level. Usually
	 * used for assigning generator types.
	 */
	@Getter protected Tile sourceTile;
	
	/**
	 * Local random number generator instance - uses the <a href="http://www.pcg-random.org/">PCG32</a> algorithm.
	 * @see <a href="http://www.pcg-random.org/">pcg-random.org</a>
	 */
	protected static final Pcg32 RAND = new Pcg32();
	/**
	 * Local random number generator instance - uses Java's inbuilt PRNG algorithm.
	 */
	protected static final Random JRAND = new Random();
	
	/**
	 * @param level The {@link Level} that this generator is generating for.
	 * @param sourceTile The tile that the Player enters this level via, typically the staircase down in the previous
	 *                   level. Usually used for assigning generator types.
	 */
	public DungeonGenerator(Level level, Tile sourceTile) {
		this.level = level;
		this.tileStore = level.tileStore;
		this.levelWidth = level.getWidth();
		this.levelHeight = level.getHeight();
		
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
	 * Quick utility method for generating a random number within a range. Uses the {@link #RAND} instance
	 * (<a href="http://www.pcg-random.org/">PCG32</a> algorithm).
	 *
	 * @param min The minimum bound for the random number (inclusive).
	 * @param max The maximum bound for the random number (exclusive).
	 *
	 * @return A (hopefully) random number within the min/max bounds.
	 */
	protected int nextInt(int min, int max) {
		return RandomUtils.random(min, max);
	}
}
