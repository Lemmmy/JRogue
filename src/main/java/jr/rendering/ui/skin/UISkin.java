package jr.rendering.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class UISkin extends Skin {
	public static UISkin instance;
	
	public UISkin() {
		instance = this;
		
		UIColours.add(this);
		UIFonts.add(this);
		UILabelStyles.add(this);
		UIButtonStyles.add(this);
		UITextButtonStyles.add(this);
		UIContainerButtonStyles.add(this);
		UITextFieldStyles.add(this);
		UIListStyles.add(this);
		UIScrollPaneStyles.add(this);
		UIListStyles.add(this);
		UIWindowStyles.add(this);
		UISplitterStyles.add(this);
	}
}
