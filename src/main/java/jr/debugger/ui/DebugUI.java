package jr.debugger.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import jr.JRogue;
import jr.Settings;
import jr.debugger.DebugClient;
import jr.debugger.ui.debugwindows.AtlasViewer;
import jr.debugger.ui.debugwindows.DebugWindow;
import jr.debugger.ui.game.GameWidget;
import jr.debugger.ui.tree.TreeNodeWidget;
import jr.dungeon.Dungeon;
import jr.rendering.base.ui.skin.UISkin;
import jr.rendering.base.ui.utils.FunctionalClickListener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DebugUI {
	private static List<DebugWindow> debugWindows = new ArrayList<>();
	
	@Getter private Skin skin;
	@Getter private Stage stage;
	
	private DebugClient debugClient;
	private Settings settings;
	@Getter private Dungeon dungeon;
	
	@Getter private List<InputProcessor> inputProcessors = new ArrayList<>();
	
	private Cell<? extends GameWidget> gameCell;
	private Cell<? extends TreeNodeWidget> rootNodeCell;
	private Cell<? extends Table> debugButtonsCell;
	private GameWidget gameWidget;
	private Table bottomBar;
	
	private GLProfiler profiler;
	private Label profileLabel;
	
	public DebugUI(DebugClient debugClient) {
		this.debugClient = debugClient;
		this.settings = JRogue.getSettings();
	}
	
	public void initialise() {
		profiler = new GLProfiler(Gdx.graphics);
		profiler.enable();
		
		ScreenViewport stageViewport = new ScreenViewport();
		stageViewport.setUnitsPerPixel(1f / settings.getHudScale());
		stage = new Stage(stageViewport);
		skin = UISkin.getInstance();
		
		addDebugWindow(new DebugWindow("Atlas Viewer", AtlasViewer.class));
		
		// stage.setDebugAll(true);
		
		Table root = new Table();
		root.setFillParent(true);
		
		initialiseTopBar(root);
		initialiseMainPane(root);
		initialiseBottomBar(root);
		
		root.top().left();
		stage.addActor(root);
		
		initInputProcessors();
	}
	
	private void initialiseTopBar(Table container) {
		Table topBar = new Table();
		profileLabel = new Label("", skin);
		profileLabel.setAlignment(Align.left);
		topBar.add(profileLabel).left();
		container.add(topBar).growX().top().left().pad(2).row();
	}
	
	private void initialiseMainPane(Table container) {
		Table main = new Table();
		initialiseGameContainer(main);
		initialiseHierarchyContainer(main);
		container.add(main).grow().top().left().row();
	}
	
	private void initialiseBottomBar(Table container) {
		bottomBar = new Table();
		initialiseDebugWindowButtons(bottomBar);
		container.add(bottomBar).growX().bottom().left().pad(2);
	}
	
	private void initialiseGameContainer(Table container) {
		Table gameContainer = new Table();
		
		gameCell = gameContainer.add(gameWidget = getNewGameWidget())
			.top().left();
		
		container.add(new ScrollPane(gameContainer.top().left(), skin))
			.grow().top().left();
	}
	
	private void initialiseHierarchyContainer(Table container) {
		Table hierarchyContainer = new Table();
		
		rootNodeCell = hierarchyContainer.add(getNewRootWidget())
			.top().left();
		
		container.add(new ScrollPane(hierarchyContainer.top(), skin))
			.minWidth(300).top().right().grow();
	}
	
	
	private void initialiseRootNode(Table container) {
		TreeNodeWidget widget = new TreeNodeWidget(debugClient, debugClient.getRootNode(), skin);
		
		if (rootNodeCell == null) {
			rootNodeCell = container.add(widget);
		} else {
			rootNodeCell.setActor(widget);
		}
	}
	
	private void initialiseDebugWindowButtons(Table container) {
		Table debugButtons = new Table();
		
		debugWindows.forEach(window -> {
			Button button = new TextButton(window.getWindowName(), skin);
			button.addListener(new FunctionalClickListener((event, x, y) -> window.show(stage, skin)));
			debugButtons.add(button).left();
		});
		
		if (debugButtonsCell == null) {
			debugButtonsCell = container.add(debugButtons).growX().bottom().left();
		} else {
			debugButtonsCell.setActor(debugButtons);
		}
	}
	
	private GameWidget getNewGameWidget() {
		if (debugClient.getDungeon() == null) return null;
		return new GameWidget(debugClient.getDungeon());
	}
	
	private TreeNodeWidget getNewRootWidget() {
		return new TreeNodeWidget(debugClient, debugClient.getRootNode(), skin);
	}
	
	public void setDungeon(Dungeon dungeon) {
		this.dungeon = dungeon;
		if (gameCell != null) gameCell.setActor(gameWidget = getNewGameWidget());
	}
	
	public void refresh() {
		if (rootNodeCell != null) {
			rootNodeCell.setActor(getNewRootWidget());
		}
		
		initialiseDebugWindowButtons(bottomBar);
	}
	
	public void initInputProcessors() {
		inputProcessors.clear();
		inputProcessors.add(stage);
	}
	
	public void render() {
		if (gameWidget != null) gameWidget.drawComponents();
		if (stage != null) stage.draw();
		
		if (profileLabel != null) profileLabel.setText(String.format(
			"Draw calls: %,d   Calls: %,d   Shader Switches: %,d   Texture Bindings: %,d   Vertex Count: %,f",
			profiler.getDrawCalls(),
			profiler.getCalls(),
			profiler.getShaderSwitches(),
			profiler.getTextureBindings(),
			profiler.getVertexCount().value
		));
		
		profiler.reset();
	}
	
	public void update(float dt) {
		if (stage != null) stage.act(dt);
	}
	
	public void resize(int width, int height) {
		if (stage != null) stage.getViewport().update(width, height, true);
	}
	
	public void dispose() {
		try {
			if (stage != null) stage.dispose();
			skin.dispose();
			profiler.disable();
		} catch (Exception ignored) {}
	}
	
	public static void addDebugWindow(DebugWindow debugWindow) {
		debugWindows.add(debugWindow);
	}
}
