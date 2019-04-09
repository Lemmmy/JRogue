package jr.rendering.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.assets.Assets;
import jr.rendering.ui.utils.TiledNinePatchDrawable;

import java.util.function.Consumer;

@UISkinStyleHandler(priority = 300)
public class UIScrollPaneStyles extends UISkinStyle {
    private NinePatchDrawable hScroll, hScrollKnob, vScroll, vScrollKnob;
    private NinePatchDrawable loweredHScroll, loweredHScrollKnob, loweredVScroll, loweredVScrollKnob;
    private TiledNinePatchDrawable loweredBackground;
    
    public UIScrollPaneStyles(UISkin skin) {
        super(skin);
    }
    
    protected void loadScrollGraphic(Assets assets, String fileName, Consumer<NinePatchDrawable> consumer) {
        loadNinePatch(assets, fileName, 2, 1, 1, 1, consumer);
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        
        loadScrollGraphic(assets, "scroll_h_scroll", n -> { hScroll = n; vScroll = n; });
        loadScrollGraphic(assets, "scroll_h_scroll_knob", n -> { hScrollKnob = n; vScrollKnob = n; });
        
        loadScrollGraphic(assets, "scroll_lowered_h_scroll", n -> { loweredHScroll = n; loweredVScroll = n; });
        loadScrollGraphic(assets, "scroll_lowered_h_scroll_knob", n -> { loweredHScrollKnob = n; loweredVScrollKnob = n; });
        
        loadTiledNinePatch(assets, "scroll_lowered_background", 1, 1, 1, 1, n -> loweredBackground = n);
    }
    
    @Override
    public void onLoaded(Assets assets) {
        super.onLoaded(assets);
        skin.add("default", getScrollPaneStyle());
        skin.add("lowered", getLoweredScrollPaneStyle());
    }
    
    public ScrollPane.ScrollPaneStyle getScrollPaneStyle() {
        ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
        
        style.hScroll = hScroll; style.hScrollKnob = hScrollKnob;
        style.vScroll = vScroll; style.vScrollKnob = vScrollKnob;
        
        return style;
    }
    
    public ScrollPane.ScrollPaneStyle getLoweredScrollPaneStyle() {
        ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
        
        style.hScroll = loweredHScroll; style.hScrollKnob = loweredHScrollKnob;
        style.vScroll = loweredVScroll; style.vScrollKnob = loweredVScrollKnob;
        
        style.background = loweredBackground;
        
        return style;
    }
}
