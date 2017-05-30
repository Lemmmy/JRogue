package jr.rendering.ui.skin;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class UISkin extends Skin {
	private static UISkin INSTANCE;
	
	private UISkin() {
		UIColours.add(this);
		UIFonts.add(this);
		UIBackgroundStyles.add(this);
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
		UIDungeonOverviewStyles.add(this);
	}
	
	public static UISkin getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new UISkin();
		}
		
		return INSTANCE;
	}
}
