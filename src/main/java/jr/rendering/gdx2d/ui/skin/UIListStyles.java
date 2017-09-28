package jr.rendering.gdx2d.ui.skin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.gdx2d.utils.ImageLoader;

public class UIListStyles {
	// TODO: still using the old style.
	
	public static void add(Skin skin) {
		skin.add("default", getListStyle(skin));
	}
	
	public static List.ListStyle getListStyle(Skin skin) {
		List.ListStyle style = new List.ListStyle();
		
		style.background = getBackground();
		style.selection = getSelection();
		
		style.font = skin.getFont("defaultNoShadow");
		style.fontColorUnselected = Colors.get("P_GREY_0");
		style.fontColorSelected = Color.WHITE;
		
		return style;
	}
	
	public static Drawable getBackground() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 84, 10, 3, 3),
			1, 1, 1, 1
		));
	}
	
	public static Drawable getSelection() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 84, 22, 3, 3),
			1, 1, 1, 1
		));
	}
}
