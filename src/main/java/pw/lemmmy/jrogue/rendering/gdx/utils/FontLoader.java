package pw.lemmmy.jrogue.rendering.gdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.GdxRuntimeException;
import pw.lemmmy.jrogue.JRogue;

import java.util.HashMap;
import java.util.Map;

public class FontLoader {
	private static final Map<String, BitmapFont> fontCache = new HashMap<>();

	public static BitmapFont getFont(String file, int size, boolean shadow) {
		String cacheString = file + "_" + size + (shadow ? "_shadow" : "");

		if (fontCache.containsKey(cacheString)) {
			return fontCache.get(cacheString);
		} else {
			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(file));
			FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

			parameter.size = size;

			if (shadow) {
				parameter.shadowColor = new Color(0.0f, 0.0f, 0.0f, 0.75f);
				parameter.shadowOffsetX = size / 16;
				parameter.shadowOffsetY = size / 16;
			}

			BitmapFont font = generator.generateFont(parameter);
			font.getData().markupEnabled = true;

			fontCache.put(cacheString, font);

			generator.dispose();

			return font;
		}
	}

	public static void disposeAll() {
		fontCache.forEach((s, f) -> {
			try {
				f.dispose();
			} catch (GdxRuntimeException e) {
				JRogue.getLogger().warn("Font \"{}\" has already been disposed!", s);
			}
		});
	}
}
