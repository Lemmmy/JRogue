package jr.rendering.gdx2d.ui.skin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

@UISkinStyleHandler
public class UITextButtonStyles extends UIButtonStyles {
	public void add(Skin skin) {
		skin.add("default", getTextButtonStyle(skin));
		skin.add("checkable", getTextButtonStyleCheckable(skin));
	}
	
	public TextButton.TextButtonStyle getTextButtonStyle(Skin skin) {
		TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
		Button.ButtonStyle buttonStyle = getButtonStyle();
		
		style.up = buttonStyle.up; style.over = buttonStyle.over;
		style.down = buttonStyle.down; style.disabled = buttonStyle.disabled;
		
		style.font = skin.getFont("defaultNoShadow");
		style.fontColor = Color.WHITE; style.downFontColor = Color.WHITE;
		style.overFontColor = Color.WHITE; style.disabledFontColor = Color.WHITE;
		
		return style;
	}
	
	public TextButton.TextButtonStyle getTextButtonStyleCheckable(Skin skin) {
		TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
		Button.ButtonStyle buttonStyle = getCheckableButtonStyle();
		
		style.up = buttonStyle.up; style.over = buttonStyle.over;
		style.down = buttonStyle.down; style.disabled = buttonStyle.disabled;
		style.checked = buttonStyle.checked; style.checkedOver = buttonStyle.checkedOver;
		
		style.font = skin.getFont("defaultNoShadow");
		style.fontColor = Color.WHITE; style.downFontColor = Color.WHITE;
		style.overFontColor = Color.WHITE; style.disabledFontColor = Color.WHITE;
		style.checkedFontColor = Color.WHITE; style.checkedOverFontColor = Color.WHITE;
		
		return style;
	}
}
