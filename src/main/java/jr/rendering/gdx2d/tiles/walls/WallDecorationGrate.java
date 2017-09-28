package jr.rendering.gdx2d.tiles.walls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.rendering.gdx2d.tiles.TileRenderer;
import jr.rendering.gdx2d.utils.ImageLoader;

import java.util.Random;

public class WallDecorationGrate extends WallDecoration {
	private static TextureRegion grate;
	
	public WallDecorationGrate() {
		grate = ImageLoader.getImageFromSheet("textures/tiles.png", 8, 2);
	}
	
	@Override
	public void draw(TileRenderer tr, SpriteBatch batch, Dungeon dungeon, int x, int y, Random random) {
		super.draw(tr, batch, dungeon, x, y, random);
		
		tr.drawTile(batch, grate, x, y);
	}
}
