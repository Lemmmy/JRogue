package jr.debugger.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import jr.JRogue;
import jr.Settings;
import jr.debugger.DebugClient;
import jr.rendering.ui.skin.UISkin;
import lombok.Getter;

public class DebugUI {
	@Getter private Skin skin;
	@Getter private Stage stage;
	
	private DebugClient debugClient;
	private Settings settings;
	
	public DebugUI(DebugClient debugClient) {
		this.debugClient = debugClient;
		this.settings = JRogue.getSettings();
	}
	
	public void initialise() {
		ScreenViewport stageViewport = new ScreenViewport();
		stageViewport.setUnitsPerPixel(1f / settings.getHudScale());
		stage = new Stage(stageViewport);
		skin = UISkin.getInstance();
		
		Table root = new Table();
		root.setFillParent(true);
		
		root.top();
		stage.addActor(root);
	}
	
	public void render() {
		if (stage != null) stage.draw();
	}
	
	public void update(float dt) {
		if (stage != null) stage.act(dt);
	}
	
	public void resize(int width, int height) {
		if (stage != null) stage.getViewport().update(width, height, true);
	}
	
	public void dispose() {
		if (stage != null) stage.dispose();
		skin.dispose();
	}
}
