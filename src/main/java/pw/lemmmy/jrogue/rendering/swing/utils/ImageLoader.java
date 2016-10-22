package pw.lemmmy.jrogue.rendering.swing.utils;

import pw.lemmmy.jrogue.JRogue;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader {
	private static final Map<String, BufferedImage> imageCache = new HashMap<>();

	public static BufferedImage getImage(String file) {
		if (imageCache.containsKey(file)) {
			return imageCache.get(file);
		} else {
			JRogue.getLogger().debug("Loading image {}", file);

			try {
				BufferedImage image = ImageIO.read(new File(file));
				imageCache.put(file, image);

				JRogue.getLogger().debug("Loaded and cached image {}", file);

				return image;
			} catch (IOException e) {
				JRogue.getLogger().error("Failed to load image {}", file);
			}

			return null;
		}
	}
}
