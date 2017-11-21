package jr.rendering.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import jr.JRogue;
import jr.rendering.gdx2d.tiles.TileMap;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ImageLoader {
	private static final Map<String, Texture> imageCache = new HashMap<>();
	
	@Getter
	private static final PixmapPacker pixmapPacker =
		new PixmapPacker(512, 512, Pixmap.Format.RGBA8888, 0, false);
	@Getter private static final TextureAtlas pixmapAtlas = new TextureAtlas();
	
	public static TextureRegion getSubimage(Texture image, int x, int y, int width, int height) {
		return new TextureRegion(image, x, y, width, height);
	}
	
	public static TextureRegion getSubimage(String image, int x, int y, int width, int height) {
		Texture sheet = ImageLoader.getImage(image);
		
		if (sheet == null) {
			JRogue.getLogger().fatal("Failed to load spritesheet {}.", image);
			System.exit(1);
		}
		
		return getSubimage(sheet, x, y, width, height);
	}
	
	public static TextureRegion getSubimage(String image) {
		Texture img = ImageLoader.getImage(image);
		
		if (img == null) {
			JRogue.getLogger().fatal("Failed to load image {}.", image);
			System.exit(1);
		}
		
		return getSubimage(img, 0, 0, img.getWidth(), img.getHeight());
	}
	
	public static Texture getImage(String file) {
		if (imageCache.containsKey(file)) {
			return imageCache.get(file);
		} else {
			Texture texture = new Texture(Gdx.files.internal(file));
			imageCache.put(file, texture);
			
			return texture;
		}
	}
	
	public static TextureRegion getImageFromSheet(String sheetName, int sheetX, int sheetY) {
		return getImageFromSheet(sheetName, sheetX, sheetY, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
	}
	
	public static TextureRegion getImageFromSheet(String sheetName, int sheetX, int sheetY, int width, int height) {
		return getImageFromSheet(sheetName, sheetX, sheetY, width, height, true);
	}
	
	public static TextureRegion getImageFromSheet(String sheetName,
												  int sheetX,
												  int sheetY,
												  int width,
												  int height,
												  boolean flipped) {
		Texture sheet = ImageLoader.getImage(sheetName);
		
		if (sheet == null) {
			JRogue.getLogger().fatal("Failed to load spritesheet {}.", sheetName);
			System.exit(1);
		}
		
		TextureRegion region = new TextureRegion(sheet, width * sheetX, height * sheetY, width, height);
		region.flip(false, flipped);
		
		return region;
	}
	
	public static void disposeAll() {
		try {
			imageCache.values().forEach(Texture::dispose);
		} catch (GdxRuntimeException e) {
			JRogue.getLogger().error("Error cleaning up game", e);
		}
	}
	
	public static Pixmap getPixmapFromTextureRegion(TextureRegion region) {
		Texture texture = region.getTexture();
		if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
		return texture.getTextureData().consumePixmap();
	}
}
