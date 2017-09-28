package jr.rendering.gdx2d.ui.skin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.gdx2d.utils.ImageLoader;

@UISkinStyleHandler
public class UITextFieldStyles implements UISkinStyle {
	public void add(Skin skin) {
		skin.add("default", getTextFieldStyle(skin));
	}
	
	public TextField.TextFieldStyle getTextFieldStyle(Skin skin) {
		TextField.TextFieldStyle style = new TextField.TextFieldStyle();
		
		style.background = getBackground();
		style.focusedBackground = getFocusedBackground();
		style.disabledBackground = getDisabledBackground();
		
		style.font = skin.getFont("defaultNoShadow");
		style.fontColor = Color.WHITE;
		
		return style;
	}
	
	public Drawable getBackground() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 138, 31, 10, 12),
			4, 5, 5, 6
		));
	}
	
	public Drawable getFocusedBackground() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 148, 31, 10, 12),
			4, 5, 5, 6
		));
	}
	
	public Drawable getDisabledBackground() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 158, 31, 10, 12),
			4, 5, 5, 6
		));
	}
}
