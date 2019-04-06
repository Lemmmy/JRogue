package jr.rendering;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import jr.ErrorHandler;
import jr.JRogue;
import jr.Settings;
import jr.debugger.DebugClient;
import jr.debugger.utils.HideFromDebugger;
import jr.dungeon.serialisation.DungeonSerialiser;
import jr.rendering.assets.Assets;
import jr.rendering.screens.BasicScreen;
import jr.rendering.screens.CharacterCreationScreen;
import jr.rendering.screens.GameScreen;
import jr.rendering.screens.utils.ScreenTransition;
import jr.utils.Profiler;
import lombok.Getter;
import lombok.Setter;

public class GameAdapter extends Game {
	/**
	 * The game's title in the game windowBorder.
	 */
	public static final String WINDOW_TITLE = "JRogue";
	
	public final Assets assets = new Assets();
	
	private Batch batch;
	
	private FrameBuffer oldFBO, newFBO;
	@Getter @Setter private Screen newScreen;
	
	private boolean transitioning;
	private float transitionDuration;
	private float currentTransitionTime;
	private boolean firstTransitionFrame = false;
	
	private ScreenTransition transition;
	
	@HideFromDebugger
	private Settings settings;
	
	@HideFromDebugger
	private Thread debugClientThread;
	
	@HideFromDebugger
	@Getter private DebugClient debugClient;
	private boolean debugWindowFocused;
	
	@HideFromDebugger
	private Object rootDebugObject;
	
	private InputMultiplexer inputMultiplexer;
	
	public GameAdapter() {
		this.settings = JRogue.getSettings();
		rootDebugObject = JRogue.INSTANCE;
	}
	
	@Override
	public void create() {
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			ErrorHandler.error(null, throwable);
			Gdx.app.exit();
		});
		
		ErrorHandler.setGLString();
		
		Profiler.time("Loading assets", () -> {
			assets.load();
			assets.syncLoad();
		});
		
		if (settings.isShowDebugClient()) {
			this.debugClientThread = new Thread(() -> this.debugClient = new DebugClient(this, rootDebugObject));
			this.debugClientThread.start();
		}
		
		batch = new SpriteBatch();
		
		oldFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		newFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		
		inputMultiplexer = new InputMultiplexer();
		
		if (DungeonSerialiser.canLoad()) {
			screen = new GameScreen(this, DungeonSerialiser.load());
		} else {
			screen = new CharacterCreationScreen(this);
		}
		
		screen.show();
		
		updateInputProcessors();
	}
	
	@Override
	public void render() {
		float delta = Gdx.graphics.getDeltaTime();
		
		if (newScreen == null) {
			// no other screen
			screen.render(delta);
		} else if (transitioning && firstTransitionFrame) {
			firstTransitionFrame = false;
		} if (transitioning && currentTransitionTime >= transitionDuration) {
			// transition is active and time limit reached
			screen.hide();
			screen = newScreen;
			screen.resume();
			screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			transitioning = false;
			newScreen = null;
			screen.render(delta);
			transitionComplete();
		} else if (transition != null && newScreen != null) {
			// transition is active
			oldFBO.begin();
			screen.render(delta);
			oldFBO.end();
			
			newFBO.begin();
			newScreen.render(delta);
			newFBO.end();
			
			float percent = currentTransitionTime / transitionDuration;
			
			transition.render(batch, oldFBO.getColorBufferTexture(), newFBO.getColorBufferTexture(), percent);
			
			currentTransitionTime += delta;
		}
	}
	
	private void transitionComplete() {
		updateInputProcessors();
	}
	
	public void updateInputProcessors() {
		inputMultiplexer.clear();
		
		if (!debugWindowFocused) {
			if (screen instanceof BasicScreen) {
				((BasicScreen) screen).getInputProcessors().forEach(inputMultiplexer::addProcessor);
			}
		} else {
			if (debugClient != null && debugClient.getUI() != null) {
				debugClient.getUI().getInputProcessors().forEach(inputMultiplexer::addProcessor);
			}
		}
		
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	@Override
	public void pause() {
		super.pause();
		
		if (screen != null) screen.pause();
		if (newScreen != null) newScreen.pause();
	}
	
	@Override
	public void resume() {
		super.resume();
		
		if (screen != null) screen.resume();
		if (newScreen != null) newScreen.resume();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		if (width < 1 || height < 1) return;
		
		if (screen != null) screen.resize(width, height);
		if (newScreen != null) newScreen.resize(width, height);
		
		batch = new SpriteBatch();
		
		oldFBO.dispose();
		newFBO.dispose();
		
		oldFBO = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
		newFBO = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
	}
	
	@Override
	public Screen getScreen() {
		return screen;
	}
	
	public void setScreen(Screen screen, ScreenTransition transition, float duration) {
		screen.show();
		
		if (transitioning) {
			JRogue.getLogger().warn("Screen changed while transition in progress");
		}
		
		if (this.screen == null) {
			this.screen = screen;
		} else {
			this.newScreen = screen;
			this.newScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			this.screen.pause();
			currentTransitionTime = 0;
			this.transition = transition;
			transitionDuration = duration;
			transitioning = true;
			firstTransitionFrame = true;
		}
		
		this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	@Override
	public void dispose() {
		if (screen != null) {
			screen.hide();
			screen.dispose();
		}
		
		if (newScreen != null) {
			newScreen.hide();
			newScreen.dispose();
		}
		
		oldFBO.dispose();
		newFBO.dispose();
		
		if (debugClient != null) debugClient.dispose();
		
		assets.dispose();
	}
	
	public void setDebugWindowFocused(boolean debugWindowFocused) {
		this.debugWindowFocused = debugWindowFocused;
		updateInputProcessors();
	}
}
