package jr.rendering.gdx2d.ui.skin;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.gdx2d.utils.ImageLoader;

public class UIDungeonOverviewStyles {
	private static final int NODE_WIDTH = 72;
	private static final int NODE_HEIGHT = 26;
	
	public static void add(Skin skin) {
		addNodeStyle(skin, "warm", 244, 0);
		addNodeStyle(skin, "mid", 244, 26);
		addNodeStyle(skin, "cold", 316, 0);
	}
	
	public static void addNodeStyle(Skin skin, String name, int x, int y) {
		skin.add(name, new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", x, y, NODE_WIDTH, NODE_HEIGHT),
			2, 2, 2, 2
		)));
	}
}
