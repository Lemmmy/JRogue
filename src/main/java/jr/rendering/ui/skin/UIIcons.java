package jr.rendering.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UIIcons {
    public static TextureRegionDrawable getIcon(Skin skin, String name) {
        return skin.get(name, TextureRegionDrawable.class);
    }
    
    public static Image getImage(Skin skin, String name) {
        return new Image(getIcon(skin, name));
    }
}
