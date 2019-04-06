package jr.rendering.tiles.walls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.rendering.assets.Assets;
import jr.rendering.tiles.TileRenderer;

import java.util.Random;

import static jr.rendering.assets.Textures.tileFile;

public class WallDecorationGrate extends WallDecoration {
	private static TextureRegion grate;
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.loadPacked(tileFile("grate"), t -> grate = t);
	}
	
	@Override
	public void draw(TileRenderer tr, SpriteBatch batch, Dungeon dungeon, int x, int y, Random random) {
		tr.drawTile(batch, grate, x, y);
	}
}
