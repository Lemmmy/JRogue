package jr.rendering.ui.windows;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import lombok.Getter;

@Getter
public abstract class PopupWindow implements Window.ResultListener {
	private final Stage stage;
	private final Skin skin;
	
	private Window window;
	
	public PopupWindow(Stage stage, Skin skin) {
		this.stage = stage;
		this.skin = skin;
	}
	
	public void show() {
		initialiseWindow();
	}
	
	private void initialiseWindow() {
		window = new Window(getTitle(), skin, this);
		
		window.addResultListener(this);
		
		window.setMovable(true);
		window.setModal(true);
		window.pad(28, 10, 10, 10);
		
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
	
	protected void remove() {}
	
	public void onResult(Object result) {}
}
