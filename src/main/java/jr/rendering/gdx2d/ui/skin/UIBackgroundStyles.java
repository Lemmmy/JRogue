package jr.rendering.gdx2d.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import jr.rendering.gdx2d.utils.ImageLoader;

@UISkinStyleHandler
public class UIBackgroundStyles implements UISkinStyle {
	public void add(Skin skin) {
		addBackground(skin, "darkBricks", 70, 89, 68, 22);
		addBackground(skin, "darkBricksBump", 70, 111, 68, 22);
	}
	
	public void addBackground(Skin skin, String name, int x, int y, int width, int height) {
		skin.add(name, new TiledDrawable(ImageLoader.getSubimage("textures/hud.png", x, y, width, height)));
	}
}
