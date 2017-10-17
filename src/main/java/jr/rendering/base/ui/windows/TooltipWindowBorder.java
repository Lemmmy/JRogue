package jr.rendering.base.ui.windows;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class TooltipWindowBorder extends WindowBorder {
	public TooltipWindowBorder(String title, Skin skin, Window owner) {
		super(title, skin, owner);
		
		setStyle(skin.get("tooltip", WindowStyle.class));
	}
	
	@Override
	protected void initialise() {
	
	}
}
