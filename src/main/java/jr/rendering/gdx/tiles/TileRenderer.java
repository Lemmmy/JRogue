package jr.rendering.gdx.tiles;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import jr.dungeon.Dungeon;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.Renderer;
import jr.rendering.gdx.GDXRenderer;
import jr.rendering.gdx.utils.ImageLoader;
import jr.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public abstract class TileRenderer {
	private static TextureRegion dim;
	private static TextureRegion dimLight;
	
	@Getter @Setter
	protected GDXRenderer renderer;
	
	@Getter @Setter
	private boolean drawingReflection = false;
	
	static {
		dim = getImageFromSheet("textures/tiles.png", 9, 1);
		dimLight = getImageFromSheet("textures/tiles.png", 10, 1);
	}
	
	protected ParticleEffectPool effectPool;
	
	protected static TextureRegion getImageFromSheet(String sheetName, int sheetX, int sheetY) {
		return ImageLoader.getImageFromSheet(sheetName, sheetX, sheetY);
	}
	
	public abstract void draw(SpriteBatch batch, Dungeon dungeon, int x, int y);
	
	public void drawExtra(SpriteBatch batch, Dungeon dungeon, int x, int y) {}
	
	public ParticleEffectPool getParticleEffectPool() {
		return effectPool;
	}
	
	protected void drawTile(SpriteBatch batch, TextureRegion image, int x, int y) {
		if (image != null) {
			int width = TileMap.TILE_WIDTH;
			int height = TileMap.TILE_HEIGHT;
			
			float tx = x * width + 0.01f;
			float ty = y * height + 0.01f;
			
			if (isDrawingReflection()) {
				batch.draw(image, tx, ty + height * 2, 0.0f, 0.0f, width, height, 1.0f, -1.0f, 0.0f);
			} else {
				batch.draw(image, tx, ty);
			}
		}
	}
	
	public void drawLight(ShapeRenderer batch, Dungeon dungeon, int x, int y) {
		int width = TileMap.TILE_WIDTH;
		int height = TileMap.TILE_HEIGHT;
		
		Color ctl = Color.BLACK;
		Color ctr = Color.BLACK;
		Color cbr = Color.BLACK;
		Color cbl = Color.BLACK;
		
		Tile tl = dungeon.getLevel().getTileStore().getTile(x, y);
		Tile tr = dungeon.getLevel().getTileStore().getTile(x + 1, y);
		Tile br = dungeon.getLevel().getTileStore().getTile(x + 1, y + 1);
		Tile bl = dungeon.getLevel().getTileStore().getTile(x, y + 1);
		
		if (tl != null && dungeon.getLevel().getVisibilityStore().isTileDiscovered(x, y)) { ctl = tl.getLightColour(); }
		if (tr != null && dungeon.getLevel().getVisibilityStore().isTileDiscovered(x + 1, y)) { ctr = tr.getLightColour(); }
		if (br != null && dungeon.getLevel().getVisibilityStore().isTileDiscovered(x + 1, y + 1)) { cbr = br.getLightColour(); }
		if (bl != null && dungeon.getLevel().getVisibilityStore().isTileDiscovered(x, y + 1)) { cbl = bl.getLightColour(); }
		
		float lx = (x + 0.5f) * width;
		float ly = (y + 0.5f) * height;
		
		batch.rect(
			lx, ly, width, height,
			Utils.awtColourToGdx(ctl, 0),
			Utils.awtColourToGdx(ctr, 1),
			Utils.awtColourToGdx(cbr, 2),
			Utils.awtColourToGdx(cbl, 3)
		);
	}
	
	public void drawDim(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		int width = TileMap.TILE_WIDTH;
		int height = TileMap.TILE_HEIGHT;
		
		if (dungeon.getLevel().getVisibilityStore().isTileInvisible(x, y)) {
			if (dungeon.getLevel().getTileStore().getTileType(x, y).getSolidity() == TileType.Solidity.SOLID) {
				batch.draw(dimLight, x * width, y * height, width, height);
			} else {
				batch.draw(dim, x * width, y * height, width, height);
			}
		}
	}
	
	public static boolean shouldDrawTile(Camera camera, int x, int y) {
		float tx = (x + 0.5f) * TileMap.TILE_WIDTH;
		float ty = (y + 0.5f) * TileMap.TILE_HEIGHT;
		
		return camera.frustum.boundsInFrustum(tx, ty, 0.0f, TileMap.TILE_WIDTH / 2, TileMap.TILE_HEIGHT / 2, 0.0f);
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
