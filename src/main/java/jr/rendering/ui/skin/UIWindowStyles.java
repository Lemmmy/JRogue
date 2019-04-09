package jr.rendering.ui.skin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.rendering.assets.Assets;
import jr.rendering.ui.utils.TiledNinePatchDrawable;

@UISkinStyleHandler
public class UIWindowStyles extends UISkinStyle {
    private TextureRegionDrawable closeUp, closeOver, closeDown;
    private TiledNinePatchDrawable background;
    private NinePatchDrawable tooltipBackground;
    
    public UIWindowStyles(UISkin skin) {
        super(skin);
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        
        loadTextureRegion(assets, "window_close_up", t -> closeUp = t);
        loadTextureRegion(assets, "window_close_over", t -> closeOver = t);
        loadTextureRegion(assets, "window_close_down", t -> closeDown = t);
        
        loadTiledNinePatch(assets, "window", 8, 8, 27, 8, n -> background = n);
        
        loadNinePatch(assets, "tooltip", 6, 6, 4, 4, n -> tooltipBackground = n);
    }
    
    @Override
    public void onLoaded(Assets assets) {
        super.onLoaded(assets);
        
        skin.add("windowCloseButton", getWindowCloseButtonStyle());
        skin.add("default", getWindowStyle(skin));
        skin.add("tooltip", getTooltipWindowStyle(skin));
    }
    
    public Button.ButtonStyle getWindowCloseButtonStyle() {
        Button.ButtonStyle style = new Button.ButtonStyle();
        
        style.up = closeUp;
        style.over = closeOver;
        style.down = closeDown;
        
        return style;
    }
    
    public Window.WindowStyle getWindowStyle(Skin skin) {
        Window.WindowStyle style = new Window.WindowStyle();
        
        style.background = background;
        
        style.titleFont = skin.getFont("default");
        style.titleFontColor = Color.WHITE;
        
        return style;
    }
    
    public Window.WindowStyle getTooltipWindowStyle(Skin skin) {
        Window.WindowStyle style = new Window.WindowStyle();
        style.background = tooltipBackground;
        return style;
    }
}
