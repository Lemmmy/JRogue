package jr.rendering.ui.skin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.assets.Assets;

import java.util.function.Consumer;

@UISkinStyleHandler
public class UITextFieldStyles extends UISkinStyle {
	private NinePatchDrawable background, focused, disabled;
	
	public UITextFieldStyles(UISkin skin) {
		super(skin);
	}
	
	protected void loadTextFieldGraphic(Assets assets, String fileName, Consumer<NinePatchDrawable> consumer) {
		loadNinePatch(assets, fileName, 4, 5, 5, 6, consumer);
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		loadTextFieldGraphic(assets, "text_field_background", n -> background = n);
		loadTextFieldGraphic(assets, "text_field_focused", n -> focused = n);
		loadTextFieldGraphic(assets, "text_field_disabled", n -> disabled = n);
	}
	
	@Override
	public void onLoaded(Assets assets) {
		super.onLoaded(assets);
		skin.add("default", getTextFieldStyle(skin));
	}
	
	public TextField.TextFieldStyle getTextFieldStyle(Skin skin) {
		TextField.TextFieldStyle style = new TextField.TextFieldStyle();
		
		style.background = background;
		style.focusedBackground = focused;
		style.disabledBackground = disabled;
		
		style.font = skin.getFont("defaultNoShadow");
		style.fontColor = Color.WHITE;
		
		return style;
	}
}
