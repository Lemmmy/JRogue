package jr.rendering.tiles.walls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.tiles.Tile;
import jr.rendering.assets.Assets;
import jr.rendering.utils.ImageUtils;
import jr.utils.Point;

import static jr.rendering.assets.Textures.tileFile;

public class TileRendererSewerWall extends TileRendererWall {
	protected static final int SHEET_WIDTH = 3;
	protected static final int SHEET_HEIGHT = 1;
	
	private static TextureRegion[] mosses = new TextureRegion[SHEET_WIDTH * SHEET_HEIGHT];
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.loadPacked(tileFile("sewer_moss"), t -> ImageUtils.loadSheet(t, mosses, SHEET_WIDTH, SHEET_HEIGHT));
	}
	
	@Override
	public TextureRegion getTextureRegion(Tile tile, Point p) {
		return getImageFromMask(getPositionMask(tile, p));
	}
	
	@Override
	public TextureRegion getTextureRegionExtra(Tile tile, Point p) {
		boolean shouldMoss = (p.x + p.y) % 3 == 0;
		
		if (isTopHorizontal(tile, p) && shouldMoss) {
			int mossNumber = (p.x + p.y) % mosses.length;
			return mosses[mossNumber];
		}
		
		return null;
	}
	
	@Override
	public void drawExtra(SpriteBatch batch, Tile tile, Point p) {
		TextureRegion t = getTextureRegionExtra(tile, p);
		drawTile(batch, t, p);
	}
}
