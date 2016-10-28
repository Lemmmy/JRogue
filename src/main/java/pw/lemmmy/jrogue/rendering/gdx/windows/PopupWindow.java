package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;

public abstract class PopupWindow {
	private Stage stage;
	private Skin skin;

	private Dungeon dungeon;
	private Level level;

	private Window window;

	public PopupWindow(Stage stage, Skin skin, Dungeon dungeon, Level level) {
		this.stage = stage;
		this.skin = skin;
		this.dungeon = dungeon;
		this.level = level;

		initialiseWindow();
	}

	private void initialiseWindow() {
		window = new Window(getTitle(), skin);

		window.setMovable(true);
		window.pad(18, 3, 3, 3);

		populateWindow();

		stage.addActor(window);
	}

	public abstract String getTitle();

	public abstract void populateWindow();
}
