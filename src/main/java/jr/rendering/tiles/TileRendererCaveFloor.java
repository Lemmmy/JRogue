package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;
import jr.rendering.utils.ImageUtils;

import java.util.Random;

import static jr.rendering.assets.Textures.tileFile;

public class TileRendererCaveFloor extends TileRendererConnecting {
	private static final int PROBABILITY_ROCK = 25;
	
	private static final int ROCK_SHEET_WIDTH = 6,
							 ROCK_SHEET_HEIGHT = 4,
						
							 ROCK_WIDTH = 8,
							 ROCK_HEIGHT = 8;
	
	private TextureRegion[] rocks = new TextureRegion[ROCK_SHEET_WIDTH * ROCK_SHEET_HEIGHT];
	
	private Random rand = new Random();
	
	public TileRendererCaveFloor() {
		super("cave_floor", "cave_wall", "cave_floor", true, TileType.TILE_GROUND, TileType.TILE_CAVE_WALL);
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.loadPacked(tileFile("rocks"), t -> ImageUtils.loadSheet(t, rocks, ROCK_SHEET_WIDTH, ROCK_SHEET_HEIGHT));
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
