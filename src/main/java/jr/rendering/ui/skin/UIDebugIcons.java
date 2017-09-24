package jr.rendering.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.rendering.utils.ImageLoader;

@UISkinStyleHandler
public class UIDebugIcons implements UISkinStyle {
	@Override
	public void add(Skin skin) {
		addIcon(skin, "debugNullIcon",  128, 200, 16, 8);
		addIcon(skin, "debugPrimitiveIcon",  144, 200, 16, 8);
		addIcon(skin, "debugStaticIcon",  40, 192, 8, 8);
		addIcon(skin, "debugFinalIcon",  48, 192, 8, 8);
		addIcon(skin, "debugEnumIcon",  56, 192, 8, 8);
	}
	
	public void addIcon(Skin skin, String name, int x, int y, int width, int height) {
		skin.add(name, new TextureRegionDrawable(ImageLoader.getSubimage("textures/hud.png", x, y, width, height)));
	}
}
