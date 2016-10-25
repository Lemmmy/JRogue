package pw.lemmmy.jrogue.rendering.gdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import pw.lemmmy.jrogue.JRogue;

import java.util.HashMap;
import java.util.Map;

public class FontLoader {
	private static final Map<String, BitmapFont> fontCache = new HashMap<>();

	public static BitmapFont getFont(String file, int size) {
		if (fontCache.containsKey(file + "_" + size)) {
			return fontCache.get(file + "_" + size);
		} else {
			JRogue.getLogger().debug("Loading font {}", file);

			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(file));
			FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
			parameter.flip = true;
			parameter.size = size;

			BitmapFont font = generator.generateFont(parameter);
			fontCache.put(file + "_" + size, font);

			generator.dispose();

			JRogue.getLogger().debug("Loaded and cached font {}", file);

			return font;
		}
	}
}
