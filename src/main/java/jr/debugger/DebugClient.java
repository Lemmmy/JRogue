package jr.debugger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.badlogic.gdx.graphics.GL20;
import jr.JRogue;
import jr.Settings;
import jr.debugger.tree.TreeNode;
import jr.debugger.ui.DebugUI;
import jr.rendering.GameAdapter;
import lombok.Getter;

public class DebugClient extends ApplicationAdapter {
	public static final String WINDOW_TITLE = "JRogue Debug Client";
	
	private GameAdapter gameAdapter;
	
	private Object rootObject;
	
	@Getter private TreeNode rootNode;
	private TreeNode pinnedNode;
	private TreeNode manuallyPinnedNode;
	
	private DebugUI ui;
	
	public DebugClient(GameAdapter gameAdapter, Object rootObject) {
		this.gameAdapter = gameAdapter;
		this.rootObject = rootObject;
		
		Settings settings = JRogue.getSettings();
		
		Lwjgl3Application app = (Lwjgl3Application) Gdx.app;
		
		Lwjgl3WindowConfiguration config = new Lwjgl3WindowConfiguration();
		config.setResizable(true);
		config.setWindowedMode(settings.getScreenWidth(), settings.getScreenHeight());
		config.setTitle(WINDOW_TITLE);
		config.setWindowListener(new Lwjgl3WindowAdapter() {
			@Override
			public void focusLost() {
				gameAdapter.setDebugWindowFocused(false);
			}
			
			@Override
			public void focusGained() {
				gameAdapter.setDebugWindowFocused(true);
			}
		});
		
		app.newWindow(this, config);
	}
	
	@Override
	public void create() {
		rootNode = new TreeNode(null, null, rootObject);
		openNode(rootNode);
		rootNode.refresh();
		
		ui = new DebugUI(this);
		ui.initialise();
		
		gameAdapter.updateInputProcessors();
	}
	
	public void openNode(TreeNode node) {
		node.open();
		pinnedNode = node;
	}
	
	public void closeNode(TreeNode node) {
		node.close();
		pinnedNode = node.getParent();
	}
	
	public void toggleNode(TreeNode node) {
		if (node.isOpen()) {
			closeNode(node);
		} else {
			openNode(node);
		}
	}
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (ui != null) {
			ui.update(Gdx.graphics.getDeltaTime());
			ui.render();
		}
	}
	
	@Override
	public void resize(int width, int height) {
		if (ui != null) ui.resize(width, height);
	}
	
	public DebugUI getUI() {
		return ui;
	}
}
