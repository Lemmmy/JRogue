package jr.rendering.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.rendering.assets.Assets;

@UISkinStyleHandler
public class UICheckBoxStyles extends UITextButtonStyles {
    protected TextureRegionDrawable off, offDisabled, on, onDisabled;
    
    public UICheckBoxStyles(UISkin skin) {
        super(skin);
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        loadTextureRegion(assets, "checkbox_off", t -> off = t);
        loadTextureRegion(assets, "checkbox_off_disabled", t -> offDisabled = t);
        loadTextureRegion(assets, "checkbox_on", t -> on = t);
        loadTextureRegion(assets, "checkbox_on_disabled", t -> onDisabled = t);
    }
    
    @Override
    public void onLoaded(Assets assets) {
        super.onLoaded(assets);
        skin.add("default", getCheckboxStyle(skin));
    }
    
    private CheckBox.CheckBoxStyle getCheckboxStyle(Skin skin) {
        CheckBox.CheckBoxStyle style = new CheckBox.CheckBoxStyle();
        
        style.checkboxOff = off;
        style.checkboxOffDisabled = offDisabled;
        style.checkboxOn = on;
        style.checkboxOnDisabled = onDisabled;
        
        applyFontStyle(style);
        
        return style;
    }
}