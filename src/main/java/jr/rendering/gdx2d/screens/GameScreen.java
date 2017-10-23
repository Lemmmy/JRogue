package jr.rendering.gdx2d.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import jr.JRogue;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.events.EntityDeathEvent;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.*;
import jr.rendering.base.components.FPSCounterComponent;
import jr.rendering.base.components.MinimapComponent;
import jr.rendering.base.components.RendererComponent;
import jr.rendering.base.components.hud.HUDComponent;
import jr.rendering.gdx2d.components.TextPopups;
import jr.rendering.base.screens.ComponentedScreen;
import jr.rendering.base.screens.DeathScreen;
import jr.rendering.base.screens.utils.SlidingTransition;
import jr.rendering.gdx2d.GameAdapter;
import jr.rendering.gdx2d.GameInputProcessor;
import jr.rendering.gdx2d.components.*;
import jr.rendering.gdx2d.tiles.TileMap;
import jr.rendering.utils.FontLoader;
import jr.rendering.utils.ImageLoader;
import jr.rendering.utils.ShaderLoader;
import jr.utils.Point;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;

/**
 * The game's renderer. Houses the {@link RendererComponent components} used for rendering, and also handles the main
 * batch and sceneCamera.
 */
@Getter
public class GameScreen extends ComponentedScreen implements EventListener {
	/**
	 * The time in seconds to animate movement between turns.
	 */
	public static final float TURN_LERP_DURATION = 0.170f;
	
	/**
	 * The {@link GameAdapter} instance.
	 */
	private GameAdapter game;
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
	 * batch that's inside the game's {@link #camera viewport sceneCamera}, and moves along with the player etc.
	 */
	private SpriteBatch mainBatch;
	
	/**
	 * The 'main sceneCamera' - the sceneCamera inside the {@link jr.dungeon.Level} that follows the {@link Player}.
	 */
	private OrthographicCamera camera;
	
	private float zoom = 1.0f;
	private float zoomRounding = 1 / zoom * TileMap.TILE_WIDTH * 4;
	
	private float renderTime;
	
	private float turnLerpTime;
	private boolean wasTurnLerping = false;
	private boolean turnLerping = false;
	
	@Getter(AccessLevel.NONE)
	private boolean dontSave = false;
	
	private SpriteBatch debugBatch;
	private OrthographicCamera debugCamera;
	private BitmapFont debugFont;
	
	/**
	 * The game's main OpenGL renderer using LibGDX.
	 *
	 * @param game The game adapter instance.
	 * @param dungeon The dungeon that should be rendered.
	 */
	public GameScreen(GameAdapter game, Dungeon dungeon) {
		super(dungeon);
		
		this.game = game;
		this.dungeon = dungeon;
		this.dungeon.eventSystem.addListener(this);
		
		JRogue.INSTANCE.setDungeon(dungeon);

		settings = JRogue.getSettings();
		
		updateWindowTitle();
		
		mainBatch = new SpriteBatch();
		
		initialiseCamera();
		
		for (TileMap tmap : TileMap.values()) {
			tmap.getRenderer().setRenderer(this);
		}
		
		debugBatch = new SpriteBatch();
		debugCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		debugFont = FontLoader.getFont("fonts/Lato-Regular.ttf", 11, false, true);
		
		dungeon.eventSystem.addListener(new TextPopups(getComponent(HUDComponent.class, "hud")));
		
		dungeon.start();
	}
	
	private void initialiseCamera() {
		zoom = 1f / settings.getZoom();
		zoomRounding = 1f / zoom * TileMap.TILE_WIDTH * 4f;
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		updateCameraZoom(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		camera.update();
		
		getRendererComponents().values().stream()
			.filter(GameComponent.class::isInstance)
			.map(GameComponent.class::cast)
			.forEach(r -> r.setCamera(camera));
	}
	
	private void updateCameraZoom(int width, int height) {
		if (!settings.isShowLevelDebug()) {
			camera.zoom = 0.5f;
		}
	}
	
	@Override
	public void initialiseComponents() {
		addComponent(10, "level", LevelComponent.class);
		addComponent(15, "particlesBelow", ParticlesComponent.Below.class);
		addComponent(20, "path", PathComponent.class);
		addComponent(30, "entities", EntityComponent.class);
		addComponent(35, "particlesAbove", ParticlesComponent.Above.class);
		
		if (!settings.isShowLevelDebug()) {
			addComponent(50, "lighting", LightingComponent.class);
		}
		
		addComponent(100, "hud", HUDComponent.class);
		addComponent(125, "minimap", MinimapComponent.class);
		
		if (settings.isShowFPSCounter()) {
			addComponent(130, "fps", FPSCounterComponent.class);
		}
	}
	
	@Override
	public Point unprojectWorldPos(float screenX, float screenY) {
		Vector3 unprojected = camera.unproject(new Vector3(screenX, screenY, 0));
		
		return new Point(
			(int) unprojected.x / TileMap.TILE_WIDTH,
			(int) unprojected.y / TileMap.TILE_HEIGHT
		);
	}
	
	@Override
	public Vector3 projectWorldPos(float worldX, float worldY) {
		return camera.project(new Vector3(
			worldX * TileMap.TILE_WIDTH,
			worldY * TileMap.TILE_HEIGHT,
			0
		));
	}
	
	private void initialiseInputProcessors() {
		clearInputProcessors();
		addInputProcessor(new GameInputProcessor(dungeon, this));
		addInputProcessor(getComponent(HUDComponent.class, "hud").getStage());
	}
	
	private void updateWindowTitle() {
		Gdx.graphics.setTitle(String.format(
			"%s - %s - Turn %,d",
			GameAdapter.WINDOW_TITLE,
			dungeon.getName(),
			dungeon.turnSystem.getTurn()
		));
	}
	
	/**
	 * Updates the in-game sceneCamera position.
	 */
	public void updateCamera() {
		Player p = dungeon.getPlayer();
		
		if (p != null && !settings.isShowLevelDebug()) {
			float worldX = p.getX();
			float worldY = p.getY();
			
			if (p.getPersistence().has("animationData")) {
				JSONObject ad = p.getPersistence().getJSONObject("animationData");
				
				worldX = p.getX() + (float) ad.optDouble("cameraX", 0);
				worldY = p.getY() + (float) ad.optDouble("cameraY", 0);
			}
			
			float camX = (worldX + 0.5f) * TileMap.TILE_WIDTH;
			float camY = worldY * TileMap.TILE_HEIGHT;
			
			camera.position.x = Math.round(camX * zoomRounding) / zoomRounding;
			camera.position.y = Math.round(camY * zoomRounding) / zoomRounding;
		}
		
		camera.update();
	}
	
	public void render(float delta) {
		renderTime += delta;
		
		if (turnLerping) turnLerpTime += delta;
		
		if (turnLerpTime >= TURN_LERP_DURATION) {
			turnLerping = false;
			turnLerpTime = 0;
			wasTurnLerping = true;
		} else {
			wasTurnLerping = false;
		}
		
		if (!settings.isShowTurnAnimations()) updateCamera();
		
		updateRendererComponents(delta);
		
		if (settings.isShowTurnAnimations()) updateCamera();
		
		mainBatch.setProjectionMatrix(camera.combined);
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT |
			(Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
		
		mainBatch.begin();
		mainBatch.enableBlending();
		
		renderMainBatchComponents(delta);
		
		mainBatch.end();
		
		renderOtherBatchComponents(delta);
		
		if (settings.isShowTurnAnimations()) updateCamera();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		camera.setToOrtho(true, width, height);
		updateCameraZoom(width, height);
		
		debugCamera.setToOrtho(true, width, height);
	}
	
	@Override
	public void show() {
		super.show();
		
		initialiseInputProcessors();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		if (settings.isAutosave() && !dontSave && dungeon.getPlayer().isAlive()) {
			dungeon.save();
		}
		
		mainBatch.dispose();
		
		ImageLoader.disposeAll();
		FontLoader.disposeAll();
		ShaderLoader.disposeAll();
		LogManager.shutdown();
	}
	
	@EventHandler
	private void onLevelChange(LevelChangeEvent e) {
		turnLerpTime = 0;
		turnLerping = false;
	}
	
	@EventHandler
	private void onBeforeTurn(BeforeTurnEvent e) {
		if (settings.isShowTurnAnimations()) {
			turnLerpTime = 0;
			turnLerping = true;
		}
	}
	
	@EventHandler
	private void onTurn(TurnEvent e) {
		updateWindowTitle();
	}
	
	@EventHandler
	private void onQuit(QuitEvent e) {
		dontSave = true;
		Gdx.app.exit();
	}
	
	@EventHandler
	private void onSaveAndQuit(SaveAndQuitEvent e) {
		Gdx.app.exit();
	}
	
	@EventHandler
	private void onPlayerDeath(EntityDeathEvent e) {
		if (!e.isVictimPlayer()) return;
		
		game.setScreen(
			new DeathScreen(game, dungeon, e),
			new SlidingTransition(
				SlidingTransition.Direction.DOWN,
				false,
				Interpolation.circle
			),
			0.5f
		);
	}
	
	public Matrix4 getCombinedTransform() {
		return camera.combined;
	}
}
