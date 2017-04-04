package jr.rendering.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.rendering.utils.FontLoader;

public class UIFonts {
	public static void addFonts(Skin skin) {
		skin.add("default", FontLoader.getFont("fonts/PixelOperator.ttf", 16, true, false));
		skin.add("defaultNoShadow", FontLoader.getFont("fonts/PixelOperator.ttf", 16, false, false));
		skin.add("large", FontLoader.getFont("fonts/PixelOperator.ttf", 32, true, false));
		skin.add("largeNoShadow", FontLoader.getFont("fonts/PixelOperator.ttf", 32, false, false));
	}
}
