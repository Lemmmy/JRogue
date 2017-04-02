package jr.rendering.components.hud.windows;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.rendering.Renderer;
import lombok.Getter;

@Getter
public abstract class PopupWindow implements Window.ResultListener {
	private final Stage stage;
	private final Skin skin;
	
	private final Dungeon dungeon;
	private final Level level;
	private final Renderer renderer;
	
	private Window window;
	
	public PopupWindow(Renderer renderer, Stage stage, Skin skin, Dungeon dungeon, Level level) {
		this.renderer = renderer;
		this.stage = stage;
		this.skin = skin;
		this.dungeon = dungeon;
		this.level = level;
	}
	
	public void show() {
		renderer.getHudComponent().addWindow(this);
		initialiseWindow();
	}
	
	private void initialiseWindow() {
		window = new Window(getTitle(), skin, this);
		
		window.addResultListener(this);
		
		window.setMovable(true);
		window.setModal(true);
		window.pad(18, 3, 3, 3);
		
		window.key(Input.Keys.ESCAPE, false);
		
		populateWindow();
		
		window.setPosition(
			(int) Math.floor(stage.getWidth() / 2) - (int) Math.floor(window.getWidth() / 2),
			(int) Math.floor(stage.getHeight() / 2) - (int) Math.floor(window.getHeight() / 2)
		);
		
		stage.addActor(window);
	}
	
	public abstract String getTitle();
	
	public abstract void populateWindow();
	
	protected void remove() {
		renderer.getHudComponent().removeWindow(this);
	}
	
	public void onResult(Object result) {}
}
