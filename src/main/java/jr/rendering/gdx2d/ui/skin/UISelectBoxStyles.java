package jr.rendering.gdx2d.ui.skin;

import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.gdx2d.utils.ImageLoader;

@UISkinStyleHandler
public class UISelectBoxStyles implements UISkinStyle {
	// TODO: new style
	
	public void add(Skin skin) {
		skin.add("default", getSelectBoxStyle(skin));
	}
	
	public SelectBox.SelectBoxStyle getSelectBoxStyle(Skin skin) {
		SelectBox.SelectBoxStyle style = new SelectBox.SelectBoxStyle();
		
		style.background = getBackground();
		style.backgroundDisabled = getBackgroundDisabled();
		style.backgroundOver = getBackgroundOver();
		style.backgroundOpen = getBackgroundOpen();
		
		style.font = skin.getFont("defaultNoShadow");
		style.fontColor = Colors.get("P_GREY_0");
		
		style.listStyle = skin.get(List.ListStyle.class);
		style.scrollStyle = skin.get(ScrollPane.ScrollPaneStyle.class);
		
		return style;
	}
	
	public Drawable getBackground() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 59, 10, 5, 18),
			2, 2, 2, 2
		));
	}
	
	public Drawable getBackgroundDisabled() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 69, 10, 5, 18),
			2, 2, 2, 2
		));
	}
	
	public Drawable getBackgroundOver() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 74, 10, 5, 18),
			2, 2, 2, 2
		));
	}
	
	public Drawable getBackgroundOpen() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 79, 10, 5, 18),
			2, 2, 2, 2
		));
	}
}
