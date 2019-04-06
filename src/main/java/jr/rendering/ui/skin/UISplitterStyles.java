package jr.rendering.ui.skin;

import jr.rendering.assets.Assets;
import org.apache.commons.lang3.StringUtils;

@UISkinStyleHandler
public class UISplitterStyles extends UISkinStyle {
	public UISplitterStyles(UISkin skin) {
		super(skin);
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		// Horizontal
		addSplitter(assets, "splitter_h_raised", "raised", false);
		addSplitter(assets, "splitter_h_lowered", "lowered", false);
		addSplitter(assets, "splitter_h_dark_raised", "darkRaised", false);
		addSplitter(assets, "splitter_h_dark_lowered", "darkLowered", false);
		
		// Vertical
		addSplitter(assets, "splitter_v_raised", "raised", true);
		addSplitter(assets, "splitter_v_lowered", "lowered", true);
		addSplitter(assets, "splitter_v_dark_raised", "darkRaised", true);
		addSplitter(assets, "splitter_v_dark_lowered", "darkLowered", true);
	}
	
	public void addSplitter(Assets assets, String fileName, String name, boolean vertical) {
		String fullName = "splitter" + (vertical ? "Vertical" : "Horizontal") + StringUtils.capitalize(name);
		
		int width = vertical ? 2 : 4; int height = vertical ? 3 : 2;
		int h = vertical ? 0 : 1; int v = vertical ? 1 : 0;
		
		loadNinePatch(assets, fileName, h, h, v, v, n -> skin.add(fullName, n));
	}
}
