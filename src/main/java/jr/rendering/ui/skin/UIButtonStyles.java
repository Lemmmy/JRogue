package jr.rendering.ui.skin;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.utils.ImageLoader;

public class UIButtonStyles {
	public static void addButtonStyles(Skin skin) {
		skin.add("default", getButtonStyle());
		skin.add("checkable", getCheckableButtonStyle());
	}
	
	public static Button.ButtonStyle getButtonStyle() {
		Button.ButtonStyle style = new Button.ButtonStyle();
		
		style.up = getButtonUp();
		style.over = getButtonOver();
		style.down = getButtonDown();
		style.disabled = getButtonDisabled();
		
		return style;
	}
	
	public static Button.ButtonStyle getCheckableButtonStyle() {
		Button.ButtonStyle style = new Button.ButtonStyle();
		
		style.up = getButtonUp();
		style.over = getButtonOver();
		style.down = getButtonDown();
		style.disabled = getButtonDisabled();
		
		style.checked = getButtonChecked();
		style.checkedOver = getButtonCheckedOver();
		
		return style;
	}
	
	public static Drawable getButtonUp() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 108, 31, 10, 12),
			4, 5, 5, 6
		));
	}
	
	public static Drawable getButtonOver() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 118, 31, 10, 12),
			4, 5, 5, 6
		));
	}
	
	public static Drawable getButtonDown() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 128, 31, 10, 12),
			4, 5, 5, 6
		));
	}
	
	public static Drawable getButtonDisabled() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 138, 31, 10, 12),
			4, 5, 5, 6
		));
	}
	
	public static Drawable getButtonChecked() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 169, 31, 10, 12),
			4, 5, 5, 6
		));
	}
	
	public static Drawable getButtonCheckedOver() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 179, 31, 10, 12),
			4, 5, 5, 6
		));
	}
}
