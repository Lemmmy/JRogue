package jr.rendering.tiles;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.TileStore;
import jr.dungeon.VisibilityStore;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileFlag;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;
import jr.rendering.assets.UsesAssets;
import jr.rendering.screens.GameScreen;
import jr.utils.Colour;
import jr.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public abstract class TileRenderer implements UsesAssets {
	private static final boolean AO_ENABLED = true;

	private static final Map<Integer, Integer[]> AO_MODES = new HashMap<>();
	private static final Colour[] AO_COLOURS = new Colour[256];

	static {
		AO_MODES.put(0, null);
		AO_MODES.put(1, new Integer[] { 180, 200, 220, 255 });
		AO_MODES.put(2, new Integer[] { 130, 170, 200, 255 });
		AO_MODES.put(3, new Integer[] { 100, 140, 170, 255 });
		AO_MODES.put(4, new Integer[] { 0, 0, 0, 0 });
		
		for (int i = 0; i <= 255; i++) {
			AO_COLOURS[i] = new Colour(i, i, i, 255);
		}
	}

	private static TextureRegion dim;
	private static TextureRegion dimLight;
	private static boolean dimLoaded = false;
	
	@Getter @Setter
	protected GameScreen renderer;
	
	@Getter @Setter
	private boolean drawingReflection = false;
	
	protected ParticleEffectPool effectPool;
	
	public static String tileFile(String fileName) {
		return "tiles/" + fileName + ".png";
	}
	
	@Override
	public void onLoad(Assets assets) {
		if (!dimLoaded) {
			assets.textures.loadPacked(tileFile("dim"), t -> dim = t);
			assets.textures.loadPacked(tileFile("dim_light"), t -> dimLight = t);
			
			dimLoaded = true;
		}
	}
	
	public abstract TextureRegion getTextureRegion(Dungeon dungeon, int x, int y);
	
	public TextureRegion getTextureRegionExtra(Dungeon dungeon, int x, int y) {
		return null;
	}
	
	public abstract void draw(SpriteBatch batch, Dungeon dungeon, int x, int y);
	
	public void drawExtra(SpriteBatch batch, Dungeon dungeon, int x, int y) {}
	
	public void drawBasic(SpriteBatch batch, Dungeon dungeon, int x, int y) {}
	
	public ParticleEffectPool getParticleEffectPool() {
		return effectPool;
	}
	
	public void drawTile(SpriteBatch batch, TextureRegion image, float x, float y) {
		if (image != null) {
			int width = TileMap.TILE_WIDTH;
			int height = TileMap.TILE_HEIGHT;
			
			float tx = x * width;
			float ty = y * height;
			
			if (isDrawingReflection()) {
				batch.draw(image, tx, ty + height * 2, 0.0f, 0.0f, width, height, 1.0f, -1.0f, 0.0f);
			} else {
				batch.draw(image, tx, ty);
			}
		}
	}


	private static int aoVal(Tile t) {
		return t == null ? 0 : (t.getType().getFlags() & TileFlag.WALL) == TileFlag.WALL ? 1 : 0;
	}

	private static Colour vAOCol(int i) {
		int rgb = AO_MODES.get(JRogue.getSettings().getAOLevel())[i];
		
		return AO_COLOURS[rgb];
	}

	private static int vAO(Tile s1, Tile s2, Tile c) {
		if (aoVal(s1) == 1 && aoVal(s2) == 1) return 0;
		
		return 3 - (aoVal(s1) + aoVal(s2) + aoVal(c));
	}
	
	public void drawLight(ShapeRenderer batch, Dungeon dungeon, int x, int y) {
		float width = TileMap.TILE_WIDTH;
		float height = TileMap.TILE_HEIGHT;
		
		Colour ctl = Colour.BLACK;
		Colour ctr = Colour.BLACK;
		Colour cbr = Colour.BLACK;
		Colour cbl = Colour.BLACK;

		Level lvl = dungeon.getLevel();
		TileStore ts = lvl.tileStore;

		Tile tl = ts.getTile(x, y);
		Tile tr = ts.getTile(x + 1, y);
		Tile br = ts.getTile(x + 1, y + 1);
		Tile bl = ts.getTile(x, y + 1);

		VisibilityStore vs = lvl.visibilityStore;

		if (tl != null && vs.isTileDiscovered(x, y)) ctl = tl.getLightColour();
		if (tr != null && vs.isTileDiscovered(x + 1, y)) ctr = tr.getLightColour();
		if (br != null && vs.isTileDiscovered(x + 1, y + 1)) cbr = br.getLightColour();
		if (bl != null && vs.isTileDiscovered(x, y + 1)) cbl = bl.getLightColour();

		float lx = (x + 0.5f) * width;
		float ly = (y + 0.5f) * height;

		// Lighting
		batch.rect(
			lx, ly, width, height,
			Utils.colourToGdx(ctl, 0),
			Utils.colourToGdx(ctr, 1),
			Utils.colourToGdx(cbr, 2),
			Utils.colourToGdx(cbl, 3)
		);

		// Ambient occlusion
		if (AO_ENABLED && tl != null && (tl.getType().getFlags() & TileFlag.WALL) != TileFlag.WALL) {
			Tile al = dungeon.getLevel().tileStore.getTile(x - 1, y);
			Tile ar = dungeon.getLevel().tileStore.getTile(x + 1, y);
			Tile at = dungeon.getLevel().tileStore.getTile(x, y - 1);
			Tile ab = dungeon.getLevel().tileStore.getTile(x, y + 1);
			Tile atl = dungeon.getLevel().tileStore.getTile(x - 1, y - 1);
			Tile atr = dungeon.getLevel().tileStore.getTile(x + 1, y - 1);
			Tile abl = dungeon.getLevel().tileStore.getTile(x - 1, y + 1);
			Tile abr = dungeon.getLevel().tileStore.getTile(x + 1, y + 1);

			int aotl = vAO(al, at, atl);
			int aotr = vAO(ar, at, atr);
			int aobl = vAO(al, ab, abl);
			int aobr = vAO(ar, ab, abr);

			Colour caotl = vAOCol(aotl);
			Colour caotr = vAOCol(aotr);
			Colour caobl = vAOCol(aobl);
			Colour caobr = vAOCol(aobr);

			batch.rect(
				x * width, y * height, width, height,
				Utils.colourToGdx(caotl, 0),
				Utils.colourToGdx(caotr, 1),
				Utils.colourToGdx(caobr, 2),
				Utils.colourToGdx(caobl, 3)
			);
		}
	}
	
	public void drawDim(SpriteBatch batch, Dungeon dungeon, int x, int y) {
		int width = TileMap.TILE_WIDTH;
		int height = TileMap.TILE_HEIGHT;
		
		if (dungeon.getLevel().visibilityStore.isTileInvisible(x, y)) {
			if (dungeon.getLevel().tileStore.getTileType(x, y).getSolidity() == TileType.Solidity.SOLID) {
				batch.draw(dimLight, x * width, y * height, width, height);
			} else {
				batch.draw(dim, x * width, y * height, width, height);
			}
		}
	}
	
	public static boolean shouldDrawTile(Camera camera, int x, int y) {
		float tx = (x + 0.5f) * TileMap.TILE_WIDTH;
		float ty = (y + 0.5f) * TileMap.TILE_HEIGHT;
		
		return camera.frustum.boundsInFrustum(
			tx, ty, 0.0f,
			TileMap.TILE_WIDTH / 2, TileMap.TILE_HEIGHT / 2, 0.0f
		) || camera.frustum.boundsInFrustum(
			tx + TileMap.TILE_WIDTH, ty + TileMap.TILE_HEIGHT, 0.0f,
			TileMap.TILE_WIDTH / 2, TileMap.TILE_HEIGHT / 2, 0.0f
		);
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
	
	public void applyParticleChanges(Dungeon dungeon, int x, int y, ParticleEffectPool.PooledEffect effect) {}
	
	public boolean canDrawBasic() {
		return false;
	}
}
