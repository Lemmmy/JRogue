package jr.debugger.ui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import jr.JRogue;
import jr.Settings;
import jr.debugger.DebugClient;
import jr.rendering.ui.skin.UISkin;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DebugUI {
	@Getter private Skin skin;
	@Getter private Stage stage;
	
	private DebugClient debugClient;
	private Settings settings;
	
	@Getter private List<InputProcessor> inputProcessors = new ArrayList<>();
	
	public DebugUI(DebugClient debugClient) {
		this.debugClient = debugClient;
		this.settings = JRogue.getSettings();
	}
	
	public void initialise() {
		ScreenViewport stageViewport = new ScreenViewport();
		stageViewport.setUnitsPerPixel(1f / settings.getHudScale());
		stage = new Stage(stageViewport);
		skin = UISkin.getInstance();
		
		// stage.setDebugAll(true);
		
		Table root = new Table();
		root.setFillParent(true);
		
		root.add(new TreeNodeWidget(debugClient.getRootNode(), skin));
		
		root.top().left();
		stage.addActor(root);
		
		initInputProcessors();
	}
	
	public void initInputProcessors() {
		inputProcessors.clear();
		inputProcessors.add(stage);
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
