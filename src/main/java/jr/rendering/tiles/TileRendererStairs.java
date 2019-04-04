package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.rendering.assets.Assets;

public class TileRendererStairs extends TileRenderer {
	private static TextureRegion up;
	private static TextureRegion down;
	private static boolean arrowsLoaded;
	
	private TextureRegion image; private String fileName;
	private StairDirection direction;
	
	public TileRendererStairs(StairDirection direction, String fileName) {
		this.direction = direction;
		this.fileName = fileName;
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		if (!arrowsLoaded) {
			assets.textures.loadPacked(tileFile("arrow_up"), t -> up = t);
			assets.textures.loadPacked(tileFile("arrow_down"), t -> down = t);
			
			arrowsLoaded = true;
		}
		
		assets.textures.loadPacked(tileFile(fileName), t -> image = t);
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		return image;
	}
	
	@Override
	public TextureRegion getTextureRegionExtra(Dungeon dungeon, int x, int y) {
		return direction == StairDirection.UP ? up : down;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		drawTile(batch, getTextureRegion(dungeon, x, y), x, y);
	}
	
	@Override
	public void drawExtra(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		TextureRegion t = getTextureRegionExtra(dungeon, x, y);
		
		if (t != null) {
			drawTile(batch, t, x, y);
		}
	}
	
	protected enum StairDirection {
		UP,
		DOWN
	}
}
