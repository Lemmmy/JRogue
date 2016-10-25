package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;

public abstract class TileRenderer {
	public abstract void draw(SpriteBatch batch, Dungeon d, int x, int y);

	protected TextureRegion getImageFromSheet(String sheetName, int sheetX, int sheetY) {
		Texture sheet = ImageLoader.getImage(sheetName);

		if (sheet == null) {
			JRogue.getLogger().fatal("Failed to load spritesheet {}.", sheetName);
			System.exit(1);
		}

		int width = TileMap.TILE_WIDTH;
		int height = TileMap.TILE_HEIGHT;

		return new TextureRegion(sheet, width * sheetX, height * sheetY, width, height);
	}

	protected void drawTile(SpriteBatch batch, TextureRegion image, int x, int y) {
		if (image != null) {
			int width = TileMap.TILE_WIDTH;
			int height = TileMap.TILE_HEIGHT;

			batch.draw(image, x * width, y * height);
		}
	}

	protected void drawLight(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		int width = TileMap.TILE_WIDTH;
		int height = TileMap.TILE_HEIGHT;

		// TODO
	}
}
