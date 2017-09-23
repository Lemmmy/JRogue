package jr.debugger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowConfiguration;
import com.badlogic.gdx.graphics.GL20;
import jr.JRogue;
import jr.Settings;
import jr.debugger.tree.TreeNode;
import jr.debugger.ui.DebugUI;
import jr.dungeon.Dungeon;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventListener;
import jr.dungeon.events.TurnEvent;
import jr.rendering.GameAdapter;
import lombok.Getter;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

public class DebugClient extends ApplicationAdapter implements EventListener {
	public static final String WINDOW_TITLE = "JRogue Debug Client";
	
	private GameAdapter gameAdapter;
	private Dungeon dungeon;
	
	private Object rootObject;
	
	@Getter private TreeNode rootNode;
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
		rootNode.open();
		
		ui = new DebugUI(this);
		ui.initialise();
		
		gameAdapter.updateInputProcessors();
	}
	
	public void toggleNode(TreeNode node) {
		if (node.isOpen()) {
			node.close();
		} else {
			node.open();
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
	
	public Set<Set<Integer>> collectOpenPaths() {
		if (rootNode == null) return new LinkedHashSet<>();
		
		return rootNode.flattened()
			.filter(TreeNode::isOpen)
			.map(TreeNode::getPath)
			.map(path -> path.stream()
				.map(TreeNode::getIdentityHashCode)
				.collect(Collectors.toCollection(LinkedHashSet::new)))
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}
	
	public void restoreOpenPaths(Set<Set<Integer>> openPaths) {
		openPaths.stream()
			.map(path -> new LinkedList<>(path).descendingIterator())
			.filter(Iterator::hasNext)
			.forEach(path -> {
				TreeNode child = rootNode;
				path.next(); // skip the first node (root node)
				
				while (path.hasNext() && (child = child.getChild(path.next())) != null) {
					child.open();
				}
			});
	}
	
	public void refreshRoot() {
		val openPaths = collectOpenPaths();
		rootNode.refresh();
		restoreOpenPaths(openPaths);
	}
	
	public void setDungeon(Dungeon dungeon) {
		this.dungeon = dungeon;
		dungeon.eventSystem.addListener(this);
		
		refreshRoot();
		ui.refresh();
	}
	
	@EventHandler
	private void onTurn(TurnEvent e) {
		refreshRoot();
		ui.refresh();
	}
}
