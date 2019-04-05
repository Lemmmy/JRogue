package jr.rendering.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.assets.Assets;

@UISkinStyleHandler
public class UITooltipStyles extends UISkinStyle {
	private NinePatchDrawable background;
	
	public UITooltipStyles(UISkin skin) {
		super(skin);
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		loadNinePatch(assets, "tooltip", 6, 6, 4, 4, n -> background = n);
	}
	
	@Override
	public void onLoaded(Assets assets) {
		super.onLoaded(assets);
		skin.add("default", getTextTooltipStyle(skin));
	}
	
	public TextTooltip.TextTooltipStyle getTextTooltipStyle(Skin skin) {
		TextTooltip.TextTooltipStyle style = new TextTooltip.TextTooltipStyle();
		
		style.background = background;
		style.label = skin.get(Label.LabelStyle.class);
		
		return style;
	}
}
