package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;

public class TileRendererDoor extends TileRenderer {
	private TextureRegion closed;

	public TileRendererDoor() {
		closed = getImageFromSheet("tiles.png", 4, 0);
	}

	@Override
	public void draw(SpriteBatch batch, Dungeon d, int x, int y) {
		drawTile(batch, closed, x, y); // TODO: Make this dependent on door state
		drawLight(batch, d, x, y);
	}
}
