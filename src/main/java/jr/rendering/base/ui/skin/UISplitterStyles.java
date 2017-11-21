package jr.rendering.base.ui.skin;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.utils.ImageLoader;
import org.apache.commons.lang3.StringUtils;

@UISkinStyleHandler
public class UISplitterStyles implements UISkinStyle {
	public void add(Skin skin) {
		// Horizontal
		addSplitter(skin, "raised", 101, 48, false);
		addSplitter(skin, "lowered", 101, 50, false);
		addSplitter(skin, "darkRaised", 101, 52, false);
		addSplitter(skin, "darkLowered", 101, 54, false);
		
		// Vertical
		addSplitter(skin, "raised", 105, 48, true);
		addSplitter(skin, "lowered", 105, 51, true);
		addSplitter(skin, "darkRaised", 105, 54, true);
		addSplitter(skin, "darkLowered", 105, 57, true);
	}
	
	public void addSplitter(Skin skin, String name, int x, int y, boolean vertical) {
		String fullName = "splitter" + (vertical ? "Vertical" : "Horizontal") + StringUtils.capitalize(name);
		
		int width = vertical ? 2 : 4;
		int height = vertical ? 3 : 2;
		
		int h = vertical ? 0 : 1;
		int v = vertical ? 1 : 0;
		
		skin.add(fullName, new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", x, y, width, height),
			h, h, v, v
		)));
	}
}
