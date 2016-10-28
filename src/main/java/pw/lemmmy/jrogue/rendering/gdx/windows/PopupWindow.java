package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
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
	}

	private void initialiseWindow() {
		window = new Window(getTitle(), skin);

		window.setMovable(true);
		window.pad(18, 3, 3, 3);

		populateWindow();

		window.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);

		stage.addActor(window);
	}

	public void show() {
		initialiseWindow();
	}

	public Stage getStage() {
		return stage;
	}

	public Skin getSkin() {
		return skin;
	}

	public Dungeon getDungeon() {
		return dungeon;
	}

	public Level getLevel() {
		return level;
	}

	public Window getWindow() {
		return window;
	}

	public abstract String getTitle();

	public abstract void populateWindow();
}
