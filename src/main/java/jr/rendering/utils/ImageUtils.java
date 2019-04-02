package jr.rendering.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.rendering.tiles.TileMap;

public class ImageUtils {
	public static TextureRegion getTile(TextureRegion sheet, int sheetX, int sheetY) {
		return getTile(sheet, sheetX, sheetY, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
	}
	
	public static TextureRegion getTile(TextureRegion sheet, int sheetX, int sheetY, int width, int height) {
		return getTile(sheet, sheetX, sheetY, width, height, true);
	}
	
	public static TextureRegion getTile(TextureRegion sheet,
										int sheetX,
										int sheetY,
										int width,
										int height,
										boolean flipped) {
		if (sheet == null) return null;
		
		TextureRegion region = new TextureRegion(sheet, width * sheetX, height * sheetY, width, height);
		region.flip(false, flipped);
		
		return region;
	}
}
