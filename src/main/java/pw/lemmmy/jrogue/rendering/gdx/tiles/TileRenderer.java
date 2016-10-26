package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Solidity;
import pw.lemmmy.jrogue.dungeon.Tile;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;
import pw.lemmmy.jrogue.utils.Utils;

import java.awt.Color;

public abstract class TileRenderer {
	private static TextureRegion dim;
	private static TextureRegion dimLight;

	static {
		dim = getImageFromSheet("tiles.png", 9, 1);
		dimLight = getImageFromSheet("tiles.png", 10, 1);
	}

	protected ParticleEffectPool effectPool;

	public abstract void draw(SpriteBatch batch, Dungeon dungeon, int x, int y);

	public ParticleEffectPool getParticleEffectPool() {
		return effectPool;
	}

	protected static TextureRegion getImageFromSheet(String sheetName, int sheetX, int sheetY) {
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

		if (tl != null && dungeon.getLevel().isTileDiscovered(x, y)) ctl = tl.getLight();
		if (tr != null && dungeon.getLevel().isTileDiscovered(x + 1, y)) ctr = tr.getLight();
		if (br != null && dungeon.getLevel().isTileDiscovered(x + 1, y + 1)) cbr = br.getLight();
		if (bl != null && dungeon.getLevel().isTileDiscovered(x, y + 1)) cbl = bl.getLight();

		batch.rect(
			x * width, y * height, width, height,
			Utils.awtColourToGdx(ctl),
			Utils.awtColourToGdx(ctr),
			Utils.awtColourToGdx(cbr),
			Utils.awtColourToGdx(cbl)
		);
	}

	public void drawDim(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		int width = TileMap.TILE_WIDTH;
		int height = TileMap.TILE_HEIGHT;

		if (!dungeon.getLevel().isTileVisible(x, y)) {
			if (dungeon.getLevel().getTile(x, y).getSolidity() == Solidity.SOLID) {
				batch.draw(dimLight, x * width, y * height, width, height);
			} else {
				batch.draw(dim, x * width, y * height, width, height);
			}
		}
	}

	public int getParticleXOffset() {
		return TileMap.TILE_WIDTH / 2;
	}

	public int getParticleYOffset() {
		return TileMap.TILE_HEIGHT / 2;
	}

	public boolean shouldDrawParticles(Dungeon dungeon, int x, int y) {
		return true;
	}
}
