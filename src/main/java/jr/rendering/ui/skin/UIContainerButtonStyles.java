package jr.rendering.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.assets.Assets;

import java.util.function.Consumer;

@UISkinStyleHandler
public class UIContainerButtonStyles extends UISkinStyle {
    protected NinePatchDrawable up, over, down;
    
    public UIContainerButtonStyles(UISkin skin) {
        super(skin);
    }
    
    protected void loadButtonGraphic(Assets assets, String fileName, Consumer<NinePatchDrawable> consumer) {
        loadNinePatch(assets, fileName, 3, 3, 3, 3, consumer);
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        loadButtonGraphic(assets, "container_button_up", n -> up = n);
        loadButtonGraphic(assets, "container_button_over", n -> over = n);
        loadButtonGraphic(assets, "container_button_down", n -> down = n);
    }
    
    @Override
    public void onLoaded(Assets assets) {
        super.onLoaded(assets);
        skin.add("containerEntry", getContainerButtonStyle());
    }
    
    public Button.ButtonStyle getContainerButtonStyle() {
        Button.ButtonStyle style = new Button.ButtonStyle();
        
        style.disabled = style.up = up;
        style.over = over;
        style.down = down;
        
        return style;
    }
}
