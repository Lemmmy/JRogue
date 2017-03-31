package jr.rendering;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import jr.ErrorHandler;
import jr.JRogue;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.*;
import jr.rendering.components.hud.HUDComponent;
import jr.rendering.components.*;
import jr.rendering.tiles.TileMap;
import jr.rendering.utils.FontLoader;
import jr.rendering.utils.ImageLoader;
import jr.rendering.utils.ShaderLoader;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The game's renderer. Houses the {@link RendererComponent components} used for rendering, and also handles the main
 * batch and camera.
 */
@Getter
public class Renderer extends ApplicationAdapter implements DungeonEventListener {
	/**
	 * The time in seconds to animate movement between turns.
	 */
	public static final float TURN_LERP_DURATION = 0.170f;
	
	/**
	 * The game's title in the game window.
	 */
	private static final String WINDOW_TITLE = "JRogue";
	
	/**
	 * The {@link Dungeon} that this renderer should render.
	 */
	private Dungeon dungeon;
	/**
	 * The user's {@link Settings}.
	 */
	private Settings settings;
	
	/**
	 * The 'main sprite batch' - the sprite batch that renders the {@link jr.dungeon.Level}'s contents. This is the
	 * batch that's inside the game's {@link #camera viewport camera}, and moves along with the player etc.
	 */
	private SpriteBatch mainBatch;
	
	/**
	 * The 'main camera' - the camera inside the {@link jr.dungeon.Level} that follows the {@link Player}.
	 */
	private OrthographicCamera camera;
	
	/**
	 * The list of renderer components - components that get a change to render to the screen at their specified
	 * Z-indexes.
	 */
	private List<RendererComponent> rendererComponents = new ArrayList<>();
	
	private LevelComponent levelComponent;
	private ParticlesComponent particlesBelowComponent;
	private PathComponent pathComponent;
	private EntityComponent entityComponent;
	private ParticlesComponent particlesAboveComponent;
	private LightingComponent lightingComponent;
	private HUDComponent hudComponent;
	private MinimapComponent minimapComponent;
	private FPSCounterComponent fpsCounterComponent;
	
	private float zoom = 1.0f;
	
	private float renderTime;
	
	private float turnLerpTime;
	private boolean turnLerping = false;
	
	@Getter(AccessLevel.NONE)
	private boolean dontSave = false;
	
	/**
	 * The game's main OpenGL renderer using LibGDX.
	 *
	 * @param dungeon The dungeon that should be rendered.
	 */
	public Renderer(Dungeon dungeon) {
		this.dungeon = dungeon;
		this.dungeon.addListener(this);

		settings = JRogue.getSettings();
		
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
		
		super.create();
		
		ErrorHandler.setGLString();
		
		updateWindowTitle();
		
		mainBatch = new SpriteBatch();
		
		initialiseCamera();
		initialiseRendererComponents();
		initialiseInputMultiplexer();
		
		dungeon.start();
	}
	
	private void initialiseCamera() {
		zoom = 24 * TileMap.TILE_WIDTH;
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		if (!settings.isShowLevelDebug()) {
			camera.viewportWidth = Math.round(zoom);
			camera.viewportHeight = Math.round(zoom * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());
		}
		
		camera.update();
	}
	
	private void initialiseRendererComponents() {
		rendererComponents.add(levelComponent = new LevelComponent(this, dungeon, settings));
		rendererComponents.add(particlesBelowComponent = new ParticlesComponent.Below(this, dungeon, settings));
		rendererComponents.add(pathComponent = new PathComponent(this, dungeon, settings));
		rendererComponents.add(entityComponent = new EntityComponent(this, dungeon, settings));
		rendererComponents.add(particlesAboveComponent = new ParticlesComponent.Above(this, dungeon, settings));
		
		if (!settings.isShowLevelDebug()) {
			rendererComponents.add(lightingComponent = new LightingComponent(this, dungeon, settings));
		}
		
		rendererComponents.add(minimapComponent = new MinimapComponent(this, dungeon, settings));
		
		if (settings.isShowFPSCounter()) {
			rendererComponents.add(fpsCounterComponent = new FPSCounterComponent(this, dungeon, settings));
		}
		
		rendererComponents.add(hudComponent = new HUDComponent(this, dungeon, settings));
		
		// add mod components
		
		rendererComponents.sort(Comparator.comparingInt(RendererComponent::getZIndex));
		
		rendererComponents.forEach(r -> r.setCamera(camera));
		rendererComponents.forEach(r -> dungeon.addListener(r));
		rendererComponents.forEach(RendererComponent::initialise);
		
		for (TileMap tmap : TileMap.values()) {
			tmap.getRenderer().setRenderer(this);
		}
	}
	
	private void initialiseInputMultiplexer() {
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(new GameInputProcessor(dungeon, this));
		inputMultiplexer.addProcessor(hudComponent.getStage());
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	private void updateWindowTitle() {
		Gdx.graphics.setTitle(String.format(
			"%s - %s - Turn %,d",
			WINDOW_TITLE,
			dungeon.getName(),
			dungeon.getTurn()
		));
	}
	
	/**
	 * Updates the in-game camera position.
	 */
	public void updateCamera() {
		Player p = dungeon.getPlayer();
		
		if (p != null && !settings.isShowLevelDebug()) {
			float worldX = p.getX() + (float) p.getPersistence().optDouble("lerpX", 0);
			float worldY = p.getY() + (float) p.getPersistence().optDouble("lerpY", 0);
			
			float camX = (worldX + 0.5f) * TileMap.TILE_WIDTH;
			float camY = worldY * TileMap.TILE_HEIGHT;
			
			camera.position.x = Math.round(camX * 100) / 100;
			camera.position.y = Math.round(camY * 100) / 100;
		}
		
		camera.update();
	}
	
	@Override
	public void render() {
		super.render();
		
		float delta = Gdx.graphics.getDeltaTime();
		renderTime += delta;
		
		if (turnLerping) {
			turnLerpTime += delta;
		}
		
		if (turnLerpTime >= TURN_LERP_DURATION) {
			turnLerping = false;
			turnLerpTime = 0;
		}
		
		if (!settings.isShowTurnAnimations()) {
			updateCamera();
		}
		
		rendererComponents.forEach(r -> r.update(delta));
		
		mainBatch.setProjectionMatrix(camera.combined);
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		mainBatch.begin();
		mainBatch.enableBlending();
		
		rendererComponents.stream()
			.filter(RendererComponent::useMainBatch)
			.forEach(r -> r.render(delta));
		
		mainBatch.end();
		
		rendererComponents.stream()
			.filter(r -> !r.useMainBatch())
			.forEach(r -> r.render(delta));
		
		if (settings.isShowTurnAnimations()) {
			updateCamera();
		}
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		camera.setToOrtho(true, width, height);
		
		if (!settings.isShowLevelDebug()) {
			camera.viewportWidth = Math.round(zoom);
			camera.viewportHeight = Math.round(zoom * height / width);
		}
		
		rendererComponents.forEach(r -> r.resize(width, height));
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		if (settings.isAutosave() && !dontSave && dungeon.getPlayer().isAlive()) {
			dungeon.save();
		}
		
		mainBatch.dispose();

		rendererComponents.forEach(RendererComponent::dispose);
		
		ImageLoader.disposeAll();
		FontLoader.disposeAll();
		ShaderLoader.disposeAll();
		LogManager.shutdown();
	}
	
	@DungeonEventHandler
	private void onLevelChange(LevelChangeEvent e) {
		turnLerpTime = 0;
		turnLerping = false;
	}
	
	@DungeonEventHandler
	private void onBeforeTurn(BeforeTurnEvent e) {
		if (settings.isShowTurnAnimations()) {
			turnLerpTime = 0;
			turnLerping = true;
		}
	}
	
	@DungeonEventHandler
	private void onTurn(TurnEvent e) {
		updateWindowTitle();
	}
	
	@DungeonEventHandler
	private void onQuit(QuitEvent e) {
		dontSave = true;
		Gdx.app.exit();
	}
	
	@DungeonEventHandler
	private void onSaveAndQuit(SaveAndQuitEvent e) {
		Gdx.app.exit();
	}
	
	public Matrix4 getCombinedTransform() {
		return camera.combined;
	}
	
	/**
	 * Panic method, called when an error occurs. Cleanly exits the game when a disaster has occurred.
	 */
	public void panic() {
		Gdx.app.exit();
	}
}
