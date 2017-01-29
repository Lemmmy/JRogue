package jr.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;

public class TileRendererSewerDrain extends TileRenderer {
	private static TextureRegion drain;
	private static TextureRegion water;
	
	public TileRendererSewerDrain() {
		if (drain == null || water == null) {
			drain = getImageFromSheet("textures/tiles.png", 15, 1);
			water = getImageFromSheet("textures/tiles.png", 15, 2);
		}
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		drawTile(batch, drain, x, y);
	}
	
	@Override
	public void drawExtra(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		super.drawExtra(batch, dungeon, x, y);
		
		if (dungeon.getLevel().getTileStore().getTileType(x, y + 1).isWater()) {
			drawTile(batch, water, x, y + 1);
		}
	}
}
