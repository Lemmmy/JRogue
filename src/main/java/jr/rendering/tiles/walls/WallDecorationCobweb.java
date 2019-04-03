package jr.rendering.tiles.walls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.rendering.assets.Assets;
import jr.rendering.tiles.TileMap;
import jr.rendering.tiles.TileRenderer;
import jr.rendering.utils.ImageUtils;

import java.util.Random;

import static jr.rendering.tiles.TileRenderer.tileFile;

public class WallDecorationCobweb extends WallDecoration {
	protected static final int SHEET_WIDTH = 2;
	protected static final int SHEET_HEIGHT = 1;
	
	private static TextureRegion[] cobwebs = new TextureRegion[SHEET_WIDTH * SHEET_HEIGHT];
	
	@Override
	public void onLoad(Assets assets) {
		assets.textures.load(tileFile("cobwebs"), t -> ImageUtils.loadSheet(new TextureRegion(t), cobwebs, SHEET_WIDTH, SHEET_HEIGHT));
	}
	
	@Override
	public void drawExtra(TileRenderer tr, SpriteBatch batch, Dungeon dungeon, int x, int y, Random rand) {
		tr.drawTile(batch, cobwebs[rand.nextInt(cobwebs.length)], x, y + 1f / TileMap.TILE_HEIGHT * 3);
	}
}
