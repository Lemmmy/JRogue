package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;

public class TileRendererStairs extends TileRenderer {
	private static TextureRegion up;
	private static TextureRegion down;
	
	private TextureRegion image;
	private StairDirection direction;
	
	public TileRendererStairs(StairDirection direction, int sheetX, int sheetY) {
		if (up == null || down == null) {
			up = getImageFromSheet("textures/tiles.png", 13, 0);
			down = getImageFromSheet("textures/tiles.png", 14, 0);
		}
		
		image = getImageFromSheet("textures/tiles.png", sheetX, sheetY);
		
		this.direction = direction;
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, int x, int y) {
		return image;
	}
	
	@Override
	public TextureRegion getTextureRegionExtra(Dungeon dungeon, int x, int y) {
		switch (direction) {
			case UP:
				return up;
			case DOWN:
				return down;
		}
		
		return null;
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
