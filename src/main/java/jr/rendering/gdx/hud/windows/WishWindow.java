package jr.rendering.gdx.hud.windows;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.rendering.gdx.GDXRenderer;

public class WishWindow extends PopupWindow {
	private TextField wishField;
	
	public WishWindow(GDXRenderer renderer, Stage stage, Skin skin, Dungeon dungeon, Level level) {
		super(renderer, stage, skin, dungeon, level);
	}
	
	@Override
	public String getTitle() {
		return "Wish";
	}
	
	@Override
	public void populateWindow() {
		getWindow().getContentTable().add(new Label("Make a wish.", getSkin(), "windowStyle")).pad(8).row();
		
		wishField = new TextField("", getSkin());
		getWindow().getContentTable().add(wishField).pad(8).width(300);
		getStage().setKeyboardFocus(wishField);
		
		getWindow().button("Wish", true);
		getWindow().key(Input.Keys.ENTER, true);
		getWindow().pack();
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
