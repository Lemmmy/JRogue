package jr.rendering.ui.skin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.rendering.ui.utils.TiledNinePatchDrawable;
import jr.rendering.utils.ImageLoader;

@UISkinStyleHandler
public class UIWindowStyles implements UISkinStyle {
	public void add(Skin skin) {
		skin.add("windowCloseButton", getWindowCloseButtonStyle());
		skin.add("default", getWindowStyle(skin));
	}
	
	public Button.ButtonStyle getWindowCloseButtonStyle() {
		Button.ButtonStyle style = new Button.ButtonStyle();
		
		style.up = getCloseButtonUp(); style.over = getCloseButtonOver();
		style.down = getCloseButtonDown();
		
		return style;
	}
	
	public Window.WindowStyle getWindowStyle(Skin skin) {
		Window.WindowStyle style = new Window.WindowStyle();
		
		style.background = getBackground();
		
		style.titleFont = skin.getFont("default");
		style.titleFontColor = Color.WHITE;
		
		return style;
	}
	
	public Drawable getCloseButtonUp() {
		return new TextureRegionDrawable(ImageLoader.getSubimage("textures/hud.png", 84, 31, 17, 17));
	}
	
	public Drawable getCloseButtonOver() {
		return new TextureRegionDrawable(ImageLoader.getSubimage("textures/hud.png", 84, 48, 17, 17));
	}
	
	public Drawable getCloseButtonDown() {
		return new TextureRegionDrawable(ImageLoader.getSubimage("textures/hud.png", 84, 65, 17, 17));
	}
	
	public Drawable getBackground() {
		return new TiledNinePatchDrawable(
			ImageLoader.getSubimage("textures/hud.png", 0, 32, 84, 57),
			8, 8, 27, 8
		);
	}
}
