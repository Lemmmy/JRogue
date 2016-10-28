package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;

public class DebugWindow extends PopupWindow {
	public DebugWindow(Stage stage, Skin skin, Dungeon dungeon, Level level) {
		super(stage, skin, dungeon, level);
	}

	@Override
	public String getTitle() {
		return "Debug Tools";
	}

	@Override
	public void populateWindow() {

	}
}
