package jr.rendering.ui.skin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import jr.rendering.assets.Assets;
import jr.rendering.ui.utils.TiledNinePatchDrawable;
import jr.rendering.utils.ImageLoader;

@UISkinStyleHandler(priority = 400)
public class UILabelStyles extends UISkinStyle {
	public UILabelStyles(UISkin skin) {
		super(skin);
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		loadTiledNinePatch(assets, "lowered_background", 1, 1, 1, 1, n -> addSimpleStyle("windowStyleLoweredMarkup", "default", null, n));
		loadTiledNinePatch(assets, "raised_background", 1, 1, 1, 1, n -> addSimpleStyle("windowStyleRaisedMarkup", "default", null, n));
	}
	
	@Override
	public void onLoaded(Assets assets) {
		addSimpleStyle("default", "default");
		addSimpleStyle("redBackground", "default", "redBackground");
		addSimpleStyle("greenBackground", "default", "greenBackground");
		addSimpleStyle("large", "large");
		addSimpleStyle("windowStyle", "default", Color.WHITE, null);
		addSimpleStyle("windowStyleMarkup", "default");
	}
	
	public void addSimpleStyle(String name, String font) {
		addSimpleStyle(name, font, null);
	}
	
	public void addSimpleStyle(String name, String font, String background) {
		addSimpleStyle(name, font, null, background != null ? skin.getDrawable(background) : null);
	}
	
	public void addSimpleStyle(String name, String font, Color fontColour, Drawable background) {
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
