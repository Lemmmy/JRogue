package jr.rendering.base.ui.windows;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.rendering.base.components.hud.HUDComponent;

public class WishWindow extends WindowBase {
	private TextField wishField;
	
	public WishWindow(HUDComponent renderer, Stage stage, Skin skin, Dungeon dungeon, Level level) {
		super(renderer, stage, skin, dungeon, level);
	}
	
	@Override
	public String getTitle() {
		return "Wish";
	}
	
	@Override
	public void populateWindow() {
		getWindowBorder().getContentTable().add(new Label("Make a wish.", getSkin(), "windowStyle")).pad(8).row();
		
		wishField = new TextField("", getSkin());
		getWindowBorder().getContentTable().add(wishField).pad(8).width(300);
		getStage().setKeyboardFocus(wishField);
		
		getWindowBorder().button("Wish", true);
		getWindowBorder().key(Input.Keys.ENTER, true);
		getWindowBorder().pack();
	}
	
	@Override
	public void onResult(Object result) {
		super.onResult(result);
		
		if (!(result instanceof Boolean) || !((Boolean) result)) {
			return;
		}
		
		String wish = wishField.getText();
		getDungeon().wish(wish);
	}
}
