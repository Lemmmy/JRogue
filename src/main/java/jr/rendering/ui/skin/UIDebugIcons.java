package jr.rendering.ui.skin;

import jr.rendering.assets.Assets;

@UISkinStyleHandler
public class UIDebugIcons extends UISkinStyle {
	public UIDebugIcons(UISkin skin) {
		super(skin);
	}
	
	public void addIcon(Assets assets, String fileName, String name) {
		loadTextureRegion(assets, fileName, t -> skin.add(name, t));
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		addIcon(assets, "debug/null", "debugNullIcon");
		addIcon(assets, "debug/primitive", "debugPrimitiveIcon");
		addIcon(assets, "debug/static", "debugStaticIcon");
		addIcon(assets, "debug/final", "debugFinalIcon");
		addIcon(assets, "debug/enum", "debugEnumIcon");
	}
}
