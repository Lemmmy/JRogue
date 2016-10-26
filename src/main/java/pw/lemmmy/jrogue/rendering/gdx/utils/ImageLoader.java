package pw.lemmmy.jrogue.rendering.gdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TileMap;

import java.util.HashMap;
import java.util.Map;

public class ImageLoader {
	private static final Map<String, Texture> imageCache = new HashMap<>();

	public static Texture getImage(String file) {
		if (imageCache.containsKey(file)) {
			return imageCache.get(file);
		} else {
			JRogue.getLogger().debug("Loading image {}", file);

			Texture texture = new Texture(Gdx.files.internal(file));
			imageCache.put(file, texture);

			JRogue.getLogger().debug("Loaded and cached image {}", file);

			return texture;
		}
	}

	public static TextureRegion getImageFromSheet(String sheetName, int sheetX, int sheetY) {
		return getImageFromSheet(sheetName, sheetX, sheetY, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
	}

	public static TextureRegion getImageFromSheet(String sheetName, int sheetX, int sheetY, int width, int height) {
		Texture sheet = ImageLoader.getImage(sheetName);

		if (sheet == null) {
			JRogue.getLogger().fatal("Failed to load spritesheet {}.", sheetName);
			System.exit(1);
		}

		TextureRegion region = new TextureRegion(sheet, width * sheetX, height * sheetY, width, height);
		region.flip(false, true);

		return region;
	}

	public static void disposeAll() {
		for (Texture texture : imageCache.values()) {
			try {
				texture.dispose();
			} catch (GdxRuntimeException ignored) {}
		}
	}
}
