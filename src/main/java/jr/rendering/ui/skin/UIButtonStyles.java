package jr.rendering.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.assets.Assets;

import java.util.function.Consumer;

@UISkinStyleHandler
public class UIButtonStyles extends UISkinStyle {
    protected NinePatchDrawable up, over, down, disabled, checked, checkedOver;
    
    public UIButtonStyles(UISkin skin) {
        super(skin);
    }
    
    protected void loadButtonGraphic(Assets assets, String fileName, Consumer<NinePatchDrawable> consumer) {
        loadNinePatch(assets, fileName, 4, 5, 5, 6, consumer);
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        loadButtonGraphic(assets, "button_up", n -> up = n);
        loadButtonGraphic(assets, "button_over", n -> over = n);
        loadButtonGraphic(assets, "button_down", n -> down = n);
        loadButtonGraphic(assets, "button_disabled", n -> disabled = n);
        loadButtonGraphic(assets, "button_checked", n -> checked = n);
        loadButtonGraphic(assets, "button_checked_over", n -> checkedOver = n);
    }
    
    @Override
    public void onLoaded(Assets assets) {
        super.onLoaded(assets);
        skin.add("default", getButtonStyle());
        skin.add("checkable", getCheckableButtonStyle());
    }
    
    public Button.ButtonStyle getButtonStyle() {
        Button.ButtonStyle style = new Button.ButtonStyle();
        
        style.up = up;
        style.over = over;
        style.down = down;
        style.disabled = disabled;
        
        return style;
    }
    
    public Button.ButtonStyle getCheckableButtonStyle() {
        Button.ButtonStyle style = new Button.ButtonStyle(getButtonStyle());
        
        style.checked = checked;
        style.checkedOver = over;
        
        return style;
    }
}
