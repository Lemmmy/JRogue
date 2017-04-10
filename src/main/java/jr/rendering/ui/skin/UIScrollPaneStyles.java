package jr.rendering.ui.skin;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.ui.utils.TiledNinePatchDrawable;
import jr.rendering.utils.ImageLoader;

public class UIScrollPaneStyles {
	public static void add(Skin skin) {
		skin.add("default", getScrollPaneStyle());
		skin.add("lowered", getLoweredScrollPaneStyle());
	}
	
	public static ScrollPane.ScrollPaneStyle getScrollPaneStyle() {
		ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
		
		style.hScroll = getHScroll(); style.hScrollKnob = getHScrollKnob();
		style.vScroll = getVScroll(); style.vScrollKnob = getVScrollKnob();
		
		return style;
	}
	
	public static ScrollPane.ScrollPaneStyle getLoweredScrollPaneStyle() {
		ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
		
		style.hScroll = getLoweredHScroll(); style.hScrollKnob = getLoweredHScrollKnob();
		style.vScroll = getLoweredVScroll(); style.vScrollKnob = getLoweredVScrollKnob();
		
		style.background = getLoweredBackground();
		
		return style;
	}
	
	public static Drawable getHScroll() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 101, 31, 7, 3),
			2, 1, 1, 1
		));
	}
	
	public static Drawable getHScrollKnob() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 101, 34, 7, 6),
			2, 1, 1, 1
		));
	}
	
	public static Drawable getVScroll() {
		return getHScroll();
	}
	
	public static Drawable getVScrollKnob() {
		return getHScrollKnob();
	}
	
	public static Drawable getLoweredBackground() {
		return new TiledNinePatchDrawable(
			ImageLoader.getSubimage("textures/hud.png", 0, 89, 70, 24),
			1, 1, 1, 1
		);
	}
	
	public static Drawable getLoweredHScroll() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 101, 41, 7, 3),
			2, 1, 1, 1
		));
	}
	
	public static Drawable getLoweredHScrollKnob() {
		return new NinePatchDrawable(new NinePatch(
			ImageLoader.getSubimage("textures/hud.png", 101, 443, 6, 5),
			2, 1, 1, 1
		));
	}
	
	public static Drawable getLoweredVScroll() {
		return getLoweredHScroll();
	}
	
	public static Drawable getLoweredVScrollKnob() {
		return getLoweredHScrollKnob();
	}
}
