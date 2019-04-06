package jr.rendering.ui.skin;

import jr.rendering.assets.Assets;

@UISkinStyleHandler
public class UIBackgroundStyles extends UISkinStyle {
	public UIBackgroundStyles(UISkin skin) {
		super(skin);
	}
	
	public void addBackground(Assets assets, String fileName, String name) {
		loadTiledDrawable(assets, fileName, t -> skin.add(name, t));
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		addBackground(assets, "dark_bricks", "darkBricks");
	}
}
