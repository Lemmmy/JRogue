package jr.rendering.tiles.walls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.rendering.tiles.TileMap;
import jr.rendering.tiles.TileRenderer;
import jr.rendering.utils.ImageLoader;

import java.util.Random;

public class WallDecorationCobweb extends WallDecoration {
	private static TextureRegion[] cobwebs;
	
	public WallDecorationCobweb() {
		cobwebs = new TextureRegion[2];
		
		for (int i = 0; i < 2; i++) {
			cobwebs[i] = ImageLoader.getImageFromSheet("textures/tiles.png", 9 + i, 2);
		}
	}
	
	@Override
	public void drawExtra(TileRenderer tr, SpriteBatch batch, Dungeon dungeon, int x, int y, Random rand) {
		super.draw(tr, batch, dungeon, x, y, rand);
		
		tr.drawTile(batch, cobwebs[rand.nextInt(cobwebs.length)], x, y + 1f / TileMap.TILE_HEIGHT * 3);
	}
}
