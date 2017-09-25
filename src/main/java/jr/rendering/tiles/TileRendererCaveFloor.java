package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;
import jr.rendering.utils.ImageLoader;

import java.util.Random;

public class TileRendererCaveFloor extends TileRendererConnecting {
	private static final int PROBABILITY_ROCK = 25;
	
	private static final int ROCK_SHEET_WIDTH = 6,
							 ROCK_SHEET_HEIGHT = 4,
						
							 ROCK_WIDTH = 8,
							 ROCK_HEIGHT = 8;
	
	private TextureRegion[] rocks;
	
	private Random rand = new Random();
	
	public TileRendererCaveFloor() {
		super(6, 2, 7, 2, true, "cavefloor", TileType.TILE_GROUND, TileType.TILE_CAVE_WALL);
		
		rocks = new TextureRegion[6 * 4];
		
		for (int i = 0; i < ROCK_SHEET_WIDTH * ROCK_SHEET_HEIGHT; i++) {
			int sx = i % ROCK_SHEET_WIDTH;
			int sy = (int) Math.floor(i / ROCK_SHEET_WIDTH);
			
			rocks[i] = ImageLoader.getImageFromSheet("textures/rocks.png", sx, sy, ROCK_WIDTH, ROCK_HEIGHT);
		}
	}
	
	@Override
	public void drawExtra(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		super.drawExtra(batch, dungeon, x, y);
		
		rand.setSeed(x * y);
		
		// i'd normally do a randFloat() comparison here but
		// for some reason there was a REALLY strange problem where any branch here with a randFloat() in it
		// would return false...
		
		if (rand.nextInt(100) <= PROBABILITY_ROCK) {
			int rockX = rand.nextInt(16),
				rockY = rand.nextInt(16);
			
			int rock = rand.nextInt(rocks.length);
			TextureRegion rockRegion = rocks[rock];
			
			batch.draw(
				rockRegion,
				x * TileMap.TILE_WIDTH + rockX,
				y * TileMap.TILE_HEIGHT + rockY
			);
		}
	}
}
