package jr.rendering;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import jr.ErrorHandler;
import jr.JRogue;
import jr.Settings;
import lombok.Getter;
import lombok.Setter;

public class GameAdapter extends Game {
	/**
	 * The game's title in the game window.
	 */
	static final String WINDOW_TITLE = "JRogue";
	
	private Batch batch;
	
	private FrameBuffer oldFBO, newFBO;
	@Getter @Setter private Screen newScreen;
	
	private boolean transitioning;
	private float transitionDuration;
	private float currentTransitionTime;
	
	private ScreenTransition transition;
	
	/**
	 * Blocking adapter constructor. Calls {@link #create()} and starts the game's loop.
	 */
	public GameAdapter() {
		Settings settings = JRogue.getSettings();
		
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setResizable(true);
		config.setWindowedMode(settings.getScreenWidth(), settings.getScreenHeight());
		config.useVsync(settings.isVsync());
		
		new Lwjgl3Application(this, config);
	}
	
	@Override
	public void create() {
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			ErrorHandler.error(null, throwable);
			Gdx.app.exit();
		});
		
		ErrorHandler.setGLString();
		
		batch = new SpriteBatch();
		
		oldFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		newFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
	
		screen = new CharacterCreationScreen(this);
		screen.show();
	}
	
	@Override
	public void render() {
		super.render();
		
		float delta = Gdx.graphics.getDeltaTime();
		
		if (newScreen == null) {
			// no other screen
			screen.render(delta);
		} else if (transitioning && currentTransitionTime >= transitionDuration) {
			// transition is active and time limit reached
			screen.hide();
			screen = newScreen;
			screen.resume();
			transitioning = false;
			newScreen = null;
			screen.render(delta);
		} else if (transition != null) {
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
		
		if (screen != null) screen.resize(width, height);
		if (newScreen != null) newScreen.resize(width, height);
		
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
			this.screen.pause();
			currentTransitionTime = 0;
			this.transition = transition;
			transitionDuration = duration;
			transitioning = true;
		}
		
		this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		if (screen != null) { screen.hide(); }
		if (newScreen != null) { newScreen.hide(); }
		
		oldFBO.dispose();
		newFBO.dispose();
	}
}
