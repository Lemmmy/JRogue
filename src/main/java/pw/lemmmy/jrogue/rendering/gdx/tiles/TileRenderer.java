package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Tile;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;
import pw.lemmmy.jrogue.utils.Utils;

import java.awt.Color;

public abstract class TileRenderer {
	public abstract void draw(SpriteBatch batch, Dungeon dungeon, int x, int y);

	protected TextureRegion getImageFromSheet(String sheetName, int sheetX, int sheetY) {
		Texture sheet = ImageLoader.getImage(sheetName);

		if (sheet == null) {
			JRogue.getLogger().fatal("Failed to load spritesheet {}.", sheetName);
			System.exit(1);
		}

		int width = TileMap.TILE_WIDTH;
		int height = TileMap.TILE_HEIGHT;

		TextureRegion region = new TextureRegion(sheet, width * sheetX, height * sheetY, width, height);
		region.flip(false, true);

		return region;
	}

	protected void drawTile(SpriteBatch batch, TextureRegion image, int x, int y) {
		if (image != null) {
			int width = TileMap.TILE_WIDTH;
			int height = TileMap.TILE_HEIGHT;

			batch.draw(image, x * width, y * height);
		}
	}

	public void drawLight(ShapeRenderer batch, Dungeon dungeon, int x, int y) {
		int width = TileMap.TILE_WIDTH;
		int height = TileMap.TILE_HEIGHT;

		Color ctl = Color.BLACK;
		Color ctr = Color.BLACK;
		Color cbr = Color.BLACK;
		Color cbl = Color.BLACK;

		Tile tl = dungeon.getLevel().getTileInfo(x, y);
		Tile tr = dungeon.getLevel().getTileInfo(x + 1, y);
		Tile br = dungeon.getLevel().getTileInfo(x + 1, y + 1);
		Tile bl = dungeon.getLevel().getTileInfo(x, y + 1);

		if (tl != null) ctl = tl.getLight();
		if (tr != null) ctr = tr.getLight();
		if (br != null) cbr = br.getLight();
		if (bl != null) cbl = bl.getLight();

		batch.rect(
			x * width, y * height, width, height,
			Utils.awtColourToGdx(ctl),
			Utils.awtColourToGdx(ctr),
			Utils.awtColourToGdx(cbr),
			Utils.awtColourToGdx(cbl)
		);
	}
}
