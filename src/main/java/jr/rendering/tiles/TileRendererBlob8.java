package jr.rendering.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.tiles.TileType;
import jr.rendering.assets.Assets;
import jr.rendering.utils.BlobUtils;
import jr.rendering.utils.ImageUtils;

import java.util.Arrays;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
import static jr.rendering.assets.Textures.blobFile;

public abstract class TileRendererBlob8 extends TileRenderer {
	protected static final int BLOB_SHEET_WIDTH = 8;
	protected static final int BLOB_SHEET_HEIGHT = 6;
	
	private static final int[][] LOCATIONS = {
		{2, 1}, {8, 2}, {10, 3}, {11, 4}, {16, 5}, {18, 6}, {22, 7}, {24, 8}, {26, 9}, {27, 10}, {30, 11}, {31, 12},
		{64, 13}, {66, 14}, {72, 15}, {74, 16}, {75, 17}, {80, 18}, {82, 19}, {86, 20}, {88, 21}, {90, 22}, {91, 23},
		{94, 24}, {95, 25}, {104, 26}, {106, 27}, {107, 28}, {120, 29}, {122, 30}, {123, 31}, {126, 32}, {127, 33},
		{208, 34}, {210, 35}, {214, 36}, {216, 37}, {218, 38}, {219, 39}, {222, 40}, {223, 41}, {248, 42}, {250, 43},
		{251, 44}, {254, 45}, {255, 46}, {0, 47}
	};
	
	private static final int[] MAP = new int[256];
	
	static {
		Arrays.fill(MAP, 0);
		
		for (int[] location : LOCATIONS) {
			MAP[location[0]] = location[1];
		}
	}
	
	protected TextureRegion[] images = new TextureRegion[BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT];
	private String fileName;
	
	private PixmapPacker pixmapPacker;
	private TextureAtlas pixmapAtlas;
	
	public TileRendererBlob8() {
		this("blob");
	}
	
	public TileRendererBlob8(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		pixmapPacker = assets.textures.getPixmapPacker();
		pixmapAtlas = assets.textures.getPixmapAtlas();
		
		if (fileName != null) {
			assets.textures.loadPacked(blobFile("blob"), t -> loadBlob(t, images));
		}
	}
	
	protected static String getBlobAtlasName(String atlasName, int i) {
		return "bakedblob_" + atlasName + "_" + i;
	}
	
	protected void loadBlob(TextureRegion sheet, TextureRegion[] set) {
		ImageUtils.loadSheet(sheet, set, BLOB_SHEET_WIDTH, BLOB_SHEET_HEIGHT);
	}
	
	protected void bakeBlobs(TextureRegion[] set, String atlasName, TextureRegion fg, TextureRegion bg) {
		Pixmap pixmapFg = ImageUtils.getPixmapFromTextureRegion(fg);
		Pixmap pixmapBg = ImageUtils.getPixmapFromTextureRegion(bg);
		Pixmap pixmapMask = ImageUtils.getPixmapFromTextureRegion(set[0]);
		
		assert fg.getRegionWidth() == bg.getRegionWidth() && fg.getRegionHeight() == bg.getRegionHeight();
		
		int width = fg.getRegionWidth();
		int height = fg.getRegionHeight();
		
		Color pixelColour = new Color();
		Color maskColour = new Color();
		
		for (int i = 0; i < set.length; i++) {
			TextureRegion mask = set[i];
			Pixmap pixmapResult = new Pixmap(width, height, Pixmap.Format.RGBA8888);
			
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					Color.rgba8888ToColor(maskColour, pixmapMask.getPixel(
						mask.getRegionX() + x,
						mask.getRegionY() - y - 1
					));
					
					if (maskColour.a > 0.5f) { // TODO: blending
						Color.rgba8888ToColor(pixelColour, pixmapBg.getPixel(
							bg.getRegionX() + x,
							bg.getRegionY() - y - 1
						));
					} else {
						Color.rgba8888ToColor(pixelColour, pixmapFg.getPixel(
							fg.getRegionX() + x,
							fg.getRegionY() - y - 1
						));
					}
					
					pixmapResult.setColor(pixelColour);
					pixmapResult.drawPixel(x, y);
				}
			}
			
			pixmapPacker.pack(getBlobAtlasName(atlasName, i), pixmapResult);
		}
		
		pixmapPacker.updateTextureAtlas(pixmapAtlas, Nearest, Nearest, false);
	}
	
	protected int getPositionMask(Level level, int x, int y) {
		return BlobUtils.getPositionMask8(this::isJoinedTile, level, x, y);
	}
	
	abstract boolean isJoinedTile(TileType tile);
	
	protected TextureRegion getImageFromMask(int mask) {
		return getImageFromMask(images, mask);
	}
	
	protected TextureRegion getImageFromMask(TextureRegion[] set, int mask) {
		return set[MAP[mask]];
	}
	
	protected TextureRegion getBakedImageFromMask(String name, int mask) {
		return pixmapAtlas.findRegion(getBlobAtlasName(name, MAP[mask]));
	}

	@Deprecated
	public void drawGenericBlob(SpriteBatch batch, Dungeon dungeon, int x, int y, TextureRegion fg, TextureRegion bg) {
		TextureRegion blobImage = getImageFromMask(getPositionMask(dungeon.getLevel(), x, y));

		drawTile(batch, fg, x, y);
		
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
		Gdx.gl.glColorMask(false, false, false, true);
		drawTile(batch, blobImage, x, y);
		
		batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
		Gdx.gl.glColorMask(true, true, true, true);
		drawTile(batch, bg, x, y);

		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void drawBakedBlob(SpriteBatch batch, Dungeon dungeon, int x, int y, String name) {
		TextureRegion blobImage = getBakedImageFromMask(name, getPositionMask(dungeon.getLevel(), x, y));
		drawTile(batch, blobImage, x, y);
	}
}
