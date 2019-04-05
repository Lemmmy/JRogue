package jr.rendering.ui.skin;

import jr.rendering.assets.Assets;

@UISkinStyleHandler
public class UIDungeonOverviewStyles extends UISkinStyle {
	public UIDungeonOverviewStyles(UISkin skin) {
		super(skin);
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		addNodeStyle(assets, "dungeon_overview_warm", "warm");
		addNodeStyle(assets, "dungeon_overview_mid", "mid");
		addNodeStyle(assets, "dungeon_overview_cold", "cold");
	}
	
	public void addNodeStyle(Assets assets, String fileName, String name) {
		loadNinePatch(assets, fileName, 2, 2, 2, 2, n -> skin.add(name, n));
	}
}
