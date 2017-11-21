package jr.rendering.base.ui.skin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.rendering.utils.ImageLoader;

@UISkinStyleHandler
public class UICheckBoxStyles implements UISkinStyle {
	@Override
	public void add(Skin skin) {
		skin.add("default", getCheckboxStyle(skin));
	}
	
	private CheckBox.CheckBoxStyle getCheckboxStyle(Skin skin) {
		CheckBox.CheckBoxStyle style = new CheckBox.CheckBoxStyle();
		
		style.checkboxOff = getCheckboxOff();
		style.checkboxOffDisabled = getCheckboxOffDisabled();
		style.checkboxOn = getCheckboxOn();
		style.checkboxOnDisabled = getCheckboxOnDisabled();
		
		style.font = skin.getFont("defaultNoShadow");
		style.fontColor = Color.WHITE; style.downFontColor = Color.WHITE;
		style.overFontColor = Color.WHITE; style.disabledFontColor = Color.WHITE;
		
		return style;
	}
	
	public Drawable getCheckboxOff() {
		return new TextureRegionDrawable(ImageLoader.getSubimage("textures/hud.png", 143, 43, 16, 16));
	}
	
	public Drawable getCheckboxOffDisabled() {
		return new TextureRegionDrawable(ImageLoader.getSubimage("textures/hud.png", 159, 43, 16, 16));
	}
	
	public Drawable getCheckboxOn() {
		return new TextureRegionDrawable(ImageLoader.getSubimage("textures/hud.png", 175, 43, 16, 16));
	}
	
	public Drawable getCheckboxOnDisabled() {
		return new TextureRegionDrawable(ImageLoader.getSubimage("textures/hud.png", 191, 43, 16, 16));
	}
}
