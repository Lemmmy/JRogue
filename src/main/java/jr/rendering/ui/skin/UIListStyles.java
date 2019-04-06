package jr.rendering.ui.skin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.assets.Assets;

@UISkinStyleHandler(priority = 300)
public class UIListStyles extends UISkinStyle {
	// TODO: still using the old style.
	protected NinePatchDrawable background, selection;
	
	public UIListStyles(UISkin skin) {
		super(skin);
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		loadNinePatch(assets, "old_list_background", 1, 1, 1, 1, n -> background = n);
		loadNinePatch(assets, "old_list_selection", 1, 1, 1, 1, n -> selection = n);
	}
	
	@Override
	public void onLoaded(Assets assets) {
		super.onLoaded(assets);
		skin.add("default", getListStyle(skin));
	}
	
	public List.ListStyle getListStyle(Skin skin) {
		List.ListStyle style = new List.ListStyle();
		
		style.background = background;
		style.selection = selection;
		
		style.font = skin.getFont("defaultNoShadow");
		style.fontColorUnselected = Colors.get("P_GREY_0");
		style.fontColorSelected = Color.WHITE;
		
		return style;
	}
}
