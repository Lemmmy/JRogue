package jr.debugger.ui.tree;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.rendering.ui.skin.UIIcons;
import lombok.Getter;

@Getter
public enum AccessLevelMap {
    UNKNOWN("Unknown"),
    PACKAGE_PRIVATE("PackagePrivate"),
    PRIVATE("Private"),
    PROTECTED("Protected"),
    PUBLIC("Public");
    
    private String iconName;
    
    AccessLevelMap(String iconName) {
        this.iconName = iconName;
    }
    
    public Image getImage(Skin skin) {
        return UIIcons.getImage(skin, "debugAccess" + iconName + "Icon");
    }
}
