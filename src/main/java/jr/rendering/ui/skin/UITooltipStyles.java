package jr.rendering.ui.skin;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.utils.ImageLoader;

@UISkinStyleHandler
public class UITooltipStyles implements UISkinStyle {
	public void add(Skin skin) {
		skin.add("default", getTextTooltipStyle(skin));
	}
	
	public TextTooltip.TextTooltipStyle getTextTooltipStyle(Skin skin) {
		TextTooltip.TextTooltipStyle style = new TextTooltip.TextTooltipStyle();
		
		style.background = getTooltipBackground();
		style.label = skin.get(Label.LabelStyle.class);
		
		return style;
	}
	
	public Drawable getTooltipBackground() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 130, 43, 13, 9),
			6, 6, 4, 4
		));
	}
}
