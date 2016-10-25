package pw.lemmmy.jrogue.rendering.gdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import pw.lemmmy.jrogue.JRogue;

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
}
