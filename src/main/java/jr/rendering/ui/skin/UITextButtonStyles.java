package jr.rendering.ui.skin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import jr.rendering.assets.Assets;

@UISkinStyleHandler
public class UITextButtonStyles extends UIButtonStyles {
    public UITextButtonStyles(UISkin skin) {
        super(skin);
    }
    
    @Override
    public void onLoaded(Assets assets) {
        super.onLoaded(assets);
        skin.add("default", getTextButtonStyle(skin));
        skin.add("checkable", getTextButtonStyleCheckable(skin));
    }
    
    public void applyFontStyle(TextButtonStyle style) {
        style.font = skin.getFont("defaultNoShadow");
        style.fontColor = Color.WHITE; style.downFontColor = Color.WHITE;
        style.overFontColor = Color.WHITE; style.disabledFontColor = Color.WHITE;
    }
    
    public TextButtonStyle getTextButtonStyle(Skin skin) {
        TextButtonStyle style = new TextButtonStyle();
        Button.ButtonStyle buttonStyle = getButtonStyle();
        
        style.up = buttonStyle.up; style.over = buttonStyle.over;
        style.down = buttonStyle.down; style.disabled = buttonStyle.disabled;
        
        applyFontStyle(style);
        
        return style;
    }
    
    public TextButtonStyle getTextButtonStyleCheckable(Skin skin) {
        TextButtonStyle style = new TextButtonStyle();
        Button.ButtonStyle buttonStyle = getCheckableButtonStyle();
        
        style.up = buttonStyle.up; style.over = buttonStyle.over;
        style.down = buttonStyle.down; style.disabled = buttonStyle.disabled;
        style.checked = buttonStyle.checked; style.checkedOver = buttonStyle.checkedOver;
        
        applyFontStyle(style);
        style.checkedFontColor = Color.WHITE; style.checkedOverFontColor = Color.WHITE;
        
        return style;
    }
}
