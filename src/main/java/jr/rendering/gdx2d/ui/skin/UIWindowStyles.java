package jr.rendering.gdx2d.ui.skin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.rendering.gdx2d.ui.utils.TiledNinePatchDrawable;
import jr.rendering.gdx2d.utils.ImageLoader;

public class UIWindowStyles {
	public static void add(Skin skin) {
		skin.add("windowCloseButton", getWindowCloseButtonStyle());
		skin.add("default", getWindowStyle(skin));
	}
	
	public static Button.ButtonStyle getWindowCloseButtonStyle() {
		Button.ButtonStyle style = new Button.ButtonStyle();
		
		style.up = getCloseButtonUp(); style.over = getCloseButtonOver();
		style.down = getCloseButtonDown();
		
		return style;
	}
	
	public static Window.WindowStyle getWindowStyle(Skin skin) {
		Window.WindowStyle style = new Window.WindowStyle();
		
		style.background = getBackground();
		
		style.titleFont = skin.getFont("default");
		style.titleFontColor = Color.WHITE;
		
		return style;
	}
	
	public static Drawable getCloseButtonUp() {
		return new TextureRegionDrawable(ImageLoader.getSubimage("textures/hud.png", 84, 31, 17, 17));
	}
	
	public static Drawable getCloseButtonOver() {
		return new TextureRegionDrawable(ImageLoader.getSubimage("textures/hud.png", 84, 48, 17, 17));
	}
	
	public static Drawable getCloseButtonDown() {
		return new TextureRegionDrawable(ImageLoader.getSubimage("textures/hud.png", 84, 65, 17, 17));
	}
	
	public static Drawable getBackground() {
		return new TiledNinePatchDrawable(
			ImageLoader.getSubimage("textures/hud.png", 0, 32, 84, 57),
			8, 8, 27, 8
		);
	}
}
