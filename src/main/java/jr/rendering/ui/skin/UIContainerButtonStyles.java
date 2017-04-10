package jr.rendering.ui.skin;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.utils.ImageLoader;

public class UIContainerButtonStyles {
	public static void add(Skin skin) {
		skin.add("containerEntry", getContainerButtonStyle());
	}
	
	public static Button.ButtonStyle getContainerButtonStyle() {
		Button.ButtonStyle style = new Button.ButtonStyle();
		
		style.disabled = style.up = getButtonUp();
		style.over = getButtonOver();
		style.down = getButtonDown();
		
		return style;
	}
	
	public static Drawable getButtonUp() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 108, 44, 7, 7),
			3, 3, 3, 3
		));
	}
	
	public static Drawable getButtonOver() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 116, 44, 7, 7),
			3, 3, 3, 3
		));
	}
	
	public static Drawable getButtonDown() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 122, 44, 7, 7),
			3, 3, 3, 3
		));
	}
}
