package jr.rendering.gdx2d.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.rendering.gdx2d.utils.FontLoader;

@UISkinStyleHandler(priority = 450)
public class UIFonts implements UISkinStyle {
	public void add(Skin skin) {
		skin.add("default", FontLoader.getFont("fonts/PixelOperator.ttf", 16, true, false));
		skin.add("defaultNoShadow", FontLoader.getFont("fonts/PixelOperator.ttf", 16, false, false));
		skin.add("large", FontLoader.getFont("fonts/PixelOperator.ttf", 32, true, false));
		skin.add("largeNoShadow", FontLoader.getFont("fonts/PixelOperator.ttf", 32, false, false));
	}
}
