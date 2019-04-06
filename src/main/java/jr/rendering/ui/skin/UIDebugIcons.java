package jr.rendering.ui.skin;

import jr.rendering.assets.Assets;

@UISkinStyleHandler
public class UIDebugIcons extends UIIconStyle {
	public UIDebugIcons(UISkin skin) {
		super(skin);
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		addIcon(assets, "debug/null", "debugNullIcon");
		addIcon(assets, "debug/primitive", "debugPrimitiveIcon");
		addIcon(assets, "debug/static", "debugStaticIcon");
		addIcon(assets, "debug/final", "debugFinalIcon");
		addIcon(assets, "debug/enum", "debugEnumIcon");
		
		addIcon(assets, "debug/access_unknown", "debugAccessUnknownIcon");
		addIcon(assets, "debug/access_package_private", "debugAccessPackagePrivateIcon");
		addIcon(assets, "debug/access_private", "debugAccessPrivateIcon");
		addIcon(assets, "debug/access_protected", "debugAccessProtectedIcon");
		addIcon(assets, "debug/access_public", "debugAccessPublicIcon");
		
		addIcon(assets, "debug/teleport", "debugTeleportIcon");
		addIcon(assets, "debug/view", "debugViewIcon");
	}
}
