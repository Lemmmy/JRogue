package jr.rendering.base.ui.windows;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import lombok.Getter;

@Getter
public abstract class Window implements WindowBorder.ResultListener {
	private final Stage stage;
	private final Skin skin;
	
	protected WindowBorder windowBorder;
	
	public Window(Stage stage, Skin skin) {
		this.stage = stage;
		this.skin = skin;
	}
	
	public void show() {
		initialiseWindow();
	}
	
	protected void initialiseWindow() {
		windowBorder = new WindowBorder(getTitle(), skin, this);
		
		windowBorder.addResultListener(this);
		
		windowBorder.setMovable(true);
		windowBorder.setModal(true);
		windowBorder.pad(28, 10, 10, 10);
		
		windowBorder.key(Input.Keys.ESCAPE, false);
		
		populateWindow();
		
		windowBorder.setPosition(
			(int) Math.floor(stage.getWidth() / 2) - (int) Math.floor(windowBorder.getWidth() / 2),
			(int) Math.floor(stage.getHeight() / 2) - (int) Math.floor(windowBorder.getHeight() / 2)
		);
		
		stage.addActor(windowBorder);
	}
	
	public abstract String getTitle();
	
	public abstract void populateWindow();
	
	protected void remove() {}
	
	public void onResult(Object result) {}
}
