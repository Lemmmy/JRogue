package jr.rendering.ui.skin;

import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.rendering.assets.Assets;

import java.util.function.Consumer;

@UISkinStyleHandler(priority = 200)
public class UISelectBoxStyles extends UISkinStyle {
    // TODO: new style
    
    private NinePatchDrawable background, disabled, over, open;
    
    public UISelectBoxStyles(UISkin skin) {
        super(skin);
    }
    
    protected void loadSelectBoxGraphic(Assets assets, String fileName, Consumer<NinePatchDrawable> consumer) {
        loadNinePatch(assets, fileName, 2, 2, 2, 2, consumer);
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        loadSelectBoxGraphic(assets, "old_select_box_background", n -> background = n);
        loadSelectBoxGraphic(assets, "old_select_box_background_disabled", n -> disabled = n);
        loadSelectBoxGraphic(assets, "old_select_box_background_over", n -> over = n);
        loadSelectBoxGraphic(assets, "old_select_box_background_open", n -> open = n);
    }
    
    @Override
    public void onLoaded(Assets assets) {
        super.onLoaded(assets);
        skin.add("default", getSelectBoxStyle(skin));
    }
    
    public SelectBox.SelectBoxStyle getSelectBoxStyle(Skin skin) {
        SelectBox.SelectBoxStyle style = new SelectBox.SelectBoxStyle();
        
        style.background = background;
        style.backgroundDisabled = disabled;
        style.backgroundOver = over;
        style.backgroundOpen = open;
        
        style.font = skin.getFont("defaultNoShadow");
        style.fontColor = Colors.get("P_GREY_0");
        
        style.listStyle = skin.get(List.ListStyle.class);
        style.scrollStyle = skin.get(ScrollPane.ScrollPaneStyle.class);
        
        return style;
    }
}
