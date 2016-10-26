package pw.lemmmy.jrogue.rendering.gdx.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Tile;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TileMap;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;
import pw.lemmmy.jrogue.utils.Utils;

import java.awt.*;

public abstract class EntityRenderer {
	public abstract void draw(SpriteBatch batch, Dungeon dungeon, Entity entity);

	protected TextureRegion getImageFromSheet(String sheetName, int sheetX, int sheetY) {
		return ImageLoader.getImageFromSheet(sheetName, sheetX, sheetY);
	}

	protected void drawTile(SpriteBatch batch, TextureRegion image, int x, int y) {
		if (image != null) {
			int width = TileMap.TILE_WIDTH;
			int height = TileMap.TILE_HEIGHT;

			batch.draw(image, x * width + 0.01f, y * height + 0.01f);
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
