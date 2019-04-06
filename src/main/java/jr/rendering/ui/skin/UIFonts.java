package jr.rendering.ui.skin;

import jr.rendering.utils.FontLoader;

@UISkinStyleHandler(priority = 450)
public class UIFonts extends UISkinStyle {
	public UIFonts(UISkin skin) {
		super(skin);
		
		// TODO: assetmanager fonts
		
		skin.add("default", FontLoader.getFont("fonts/PixelOperator.ttf", 16, true));
		skin.add("defaultNoShadow", FontLoader.getFont("fonts/PixelOperator.ttf", 16, false));
		skin.add("large", FontLoader.getFont("fonts/PixelOperator.ttf", 32, true));
		skin.add("largeNoShadow", FontLoader.getFont("fonts/PixelOperator.ttf", 32, false));
	}
}
