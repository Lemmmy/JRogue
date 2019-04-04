package jr.rendering.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.rendering.tiles.TileMap;

public class ImageUtils {
	public static TextureRegion getTile(TextureRegion sheet, int sheetX, int sheetY) {
		return getTile(sheet, sheetX, sheetY, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
	}
	
	public static TextureRegion getTile(TextureRegion sheet, int sheetX, int sheetY, int width, int height) {
		if (sheet == null) return null;
		return new TextureRegion(sheet, width * sheetX, height * sheetY, width, height);
	}
	
	public static void loadSheet(TextureRegion sheet, TextureRegion[] images, int width, int height) {
		for (int i = 0; i < images.length; i++) {
			images[i] = ImageUtils.getTile(sheet, i % width, i / width);
		}
	}
	
	public static Pixmap getPixmapFromTextureRegion(TextureRegion region) {
		Texture texture = region.getTexture();
		if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
		return texture.getTextureData().consumePixmap();
	}
}
