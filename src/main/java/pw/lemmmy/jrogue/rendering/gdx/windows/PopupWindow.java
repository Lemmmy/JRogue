package pw.lemmmy.jrogue.rendering.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.rendering.gdx.GDXRenderer;

public abstract class PopupWindow implements Window.ResultListener {
	private final Stage stage;
	private final Skin skin;

	private final Dungeon dungeon;
	private final Level level;
	private final GDXRenderer renderer;

	private Window window;

	public PopupWindow(GDXRenderer renderer, Stage stage, Skin skin, Dungeon dungeon, Level level) {
		this.renderer = renderer;
		this.stage = stage;
		this.skin = skin;
		this.dungeon = dungeon;
		this.level = level;
	}

	private void initialiseWindow() {
		window = new Window(getTitle(), skin);

		window.addResultListener(this);

		window.setMovable(true);
		window.pad(18, 3, 3, 3);

		populateWindow();

		window.setPosition((int) (stage.getWidth() / 2), (int) (stage.getHeight() / 2), Align.center);

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

	public GDXRenderer getRenderer() {
		return renderer;
	}

	public abstract String getTitle();

	public abstract void populateWindow();

	public void onResult(Object result) {}
}
