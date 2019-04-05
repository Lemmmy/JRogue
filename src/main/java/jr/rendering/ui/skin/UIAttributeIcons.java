package jr.rendering.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.dungeon.entities.player.Attribute;
import jr.rendering.assets.Assets;
import org.apache.commons.text.WordUtils;

@UISkinStyleHandler
public class UIAttributeIcons extends UIIconStyle {
	public UIAttributeIcons(UISkin skin) {
		super(skin);
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		for (Attribute attribute : Attribute.values()) {
			addIcon(assets, "attribute_" + attribute.name().toLowerCase(), getIconName(attribute));
		}
	}
	
	public static String getIconName(Attribute attribute) {
		return "attribute" + WordUtils.capitalize(attribute.name().toLowerCase());
	}
	
	public static TextureRegionDrawable getIcon(Skin skin, Attribute attribute) {
		return skin.get(getIconName(attribute), TextureRegionDrawable.class);
	}
	
	public static Image getImage(Skin skin, Attribute attribute) {
		return new Image(getIcon(skin, attribute));
	}
}
