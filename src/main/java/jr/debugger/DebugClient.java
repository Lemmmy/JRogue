package jr.debugger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowConfiguration;
import jr.JRogue;
import jr.Settings;
import jr.debugger.tree.TreeNode;
import jr.debugger.ui.DebugUI;
import lombok.Getter;

public class DebugClient extends ApplicationAdapter {
	public static final String WINDOW_TITLE = "JRogue Debug Client";
	
	private Object rootObject;
	
	@Getter private TreeNode rootNode;
	private TreeNode pinnedNode;
	private TreeNode manuallyPinnedNode;
	
	private DebugUI ui;
	
	public DebugClient(Object rootObject) {
		this.rootObject = rootObject;
		
		Settings settings = JRogue.getSettings();
		
		Lwjgl3Application app = (Lwjgl3Application) Gdx.app;
		
		Lwjgl3WindowConfiguration config = new Lwjgl3WindowConfiguration();
		config.setResizable(true);
		config.setWindowedMode(settings.getScreenWidth(), settings.getScreenHeight());
		config.setTitle(WINDOW_TITLE);
		
		app.newWindow(this, config);
	}
	
	@Override
	public void create() {
		rootNode = new TreeNode(null, null, rootObject);
		openNode(rootNode);
		rootNode.refresh();
		
		ui = new DebugUI(this);
		ui.initialise();
	}
	
	public void openNode(TreeNode node) {
		node.open();
		pinnedNode = node;
	}
	
	@Override
	public void render() {
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
