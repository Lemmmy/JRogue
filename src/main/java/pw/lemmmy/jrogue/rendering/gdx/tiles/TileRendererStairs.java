package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;

public class TileRendererStairs extends TileRenderer {
	private static TextureRegion up;
	private static TextureRegion down;

	private TextureRegion image;
	private StairDirection direction;

	public TileRendererStairs(StairDirection direction, int sheetX, int sheetY) {
		if (up == null || down == null) {
			up = getImageFromSheet("tiles.png", 13, 0);
			down = getImageFromSheet("tiles.png", 14, 0);
		}

		image = getImageFromSheet("tiles.png", sheetX, sheetY);

		this.direction = direction;
	}

	@Override
	public void draw(SpriteBatch batch, Dungeon d, int x, int y) {
		drawTile(batch, image, x, y);
		drawLight(batch, d, x, y);

		switch (direction) {
			case UP:
				drawTile(batch, up, x, y);
				break;
			case DOWN:
				drawTile(batch, down, x, y);
				break;
		}
	}

	protected enum StairDirection {
		UP,
		DOWN
	}
}
