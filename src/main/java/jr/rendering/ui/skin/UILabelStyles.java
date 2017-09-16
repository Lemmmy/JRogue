package jr.rendering.ui.skin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import jr.rendering.ui.utils.TiledNinePatchDrawable;
import jr.rendering.utils.ImageLoader;

@UISkinStyleHandler(priority = UISkinStylePriority.HIGH)
public class UILabelStyles implements UISkinStyle {
	public void add(Skin skin) {
		addSimpleStyle(skin, "default", "default");
		addSimpleStyle(skin, "redBackground", "default", "redBackground");
		addSimpleStyle(skin, "greenBackground", "default", "greenBackground");
		addSimpleStyle(skin, "large", "large");
		addSimpleStyle(skin, "windowStyle", "default", Color.WHITE, null);
		addSimpleStyle(skin, "windowStyleMarkup", "default");
		addSimpleStyle(skin, "windowStyleLoweredMarkup", "default", null, getLoweredWindowDrawable());
		addSimpleStyle(skin, "windowStyleRaisedMarkup", "default", null, getRaisedWindowDrawable());
	}
	
	public void addSimpleStyle(Skin skin, String name, String font) {
		addSimpleStyle(skin, name, font, null);
	}
	
	public void addSimpleStyle(Skin skin, String name, String font, String background) {
		addSimpleStyle(skin, name, font, null, background != null ? skin.getDrawable(background) : null);
	}
	
	public void addSimpleStyle(Skin skin, String name, String font, Color fontColour, Drawable background) {
		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = skin.getFont(font);
		labelStyle.fontColor = fontColour;
		
		if (background != null) {
			labelStyle.background = background;
		}
		
		skin.add(name, labelStyle);
	}
	
	public Drawable getLoweredWindowDrawable() {
		return new TiledNinePatchDrawable(
			ImageLoader.getSubimage("textures/hud.png", 0, 89, 70, 24),
			1, 1, 1, 1
		);
	}
	
	public Drawable getRaisedWindowDrawable() {
		return new TiledNinePatchDrawable(
			ImageLoader.getSubimage("textures/hud.png", 0, 113, 70, 24),
			1, 1, 1, 1
		);
	}
}
