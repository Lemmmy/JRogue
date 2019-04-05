package jr.rendering.ui.skin;

import jr.rendering.assets.Assets;

public abstract class UIIconStyle extends UISkinStyle {
	public UIIconStyle(UISkin skin) {
		super(skin);
	}
	
	public void addIcon(Assets assets, String fileName, String name) {
		loadTextureRegion(assets, fileName, t -> skin.add(name, t));
	}
}
