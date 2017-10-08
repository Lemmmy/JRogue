package jr.rendering.gdxvox.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import jr.JRogue;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.events.EventListener;
import jr.rendering.gdx2d.GameAdapter;
import jr.rendering.gdx2d.components.FPSCounterComponent;
import jr.rendering.gdx2d.screens.BasicScreen;
import jr.rendering.gdx2d.utils.FontLoader;
import jr.rendering.gdxvox.objects.entities.EntityRendererMap;
import jr.rendering.gdxvox.objects.tiles.TileRendererMap;
import jr.rendering.gdxvox.utils.SceneContext;

public class VoxGameScreen extends BasicScreen implements EventListener {
	private static final float VIEWPORT_SIZE = 20;
	
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
	
	private SceneContext sceneContext;
	private TileRendererMap tileRendererMap;
	private EntityRendererMap entityRendererMap;
	
	
	// private OrthographicCamera camera;
	private PerspectiveCamera camera;
	private CameraInputController controller;
	
	private SpriteBatch debugBatch;
	private BitmapFont debugFont;
	private FPSCounterComponent fpsCounterComponent;
	
	public VoxGameScreen(GameAdapter game, Dungeon dungeon) {
		this.game = game;
		this.dungeon = dungeon;
		this.dungeon.eventSystem.addListener(this);
		
		JRogue.INSTANCE.setDungeon(dungeon);
		
		settings = JRogue.getSettings();
		
		initialise();
		
		dungeon.start();
	}
	
	private void initialise() {
		sceneContext = new SceneContext(dungeon);
		
		tileRendererMap = new TileRendererMap(sceneContext);
		tileRendererMap.initialise();
		dungeon.eventSystem.addListener(tileRendererMap);
		
		entityRendererMap = new EntityRendererMap(sceneContext);
		entityRendererMap.initialise();
		dungeon.eventSystem.addListener(entityRendererMap);
		
		// camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		updateCameraViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.lookAt(20, 1, 20);
		
		controller = new CameraInputController(camera);
		addInputProcessor(controller);
		
		debugBatch = new SpriteBatch();
		debugFont = FontLoader.getFont("fonts/Lato-Regular.ttf", 12, true, false);
		
		fpsCounterComponent = new FPSCounterComponent(null, dungeon, settings);
		fpsCounterComponent.initialise();
		
		updateWindowTitle();
	}
	
	private void updateWindowTitle() {
		Gdx.graphics.setTitle(String.format(
			"%s - %s - Turn %,d",
			GameAdapter.WINDOW_TITLE,
			dungeon.getName(),
			dungeon.turnSystem.getTurn()
		));
	}
	
	private void updateCameraViewport(float width, float height) {
		float aspectRatio = width / height;
		// camera.setToOrtho(false, VIEWPORT_SIZE * aspectRatio, VIEWPORT_SIZE);
	}
	
	private void updateCamera() {
		// camera.position.set(20f, 20f, 20f);
		// camera.direction.set(-0.69631064f, -0.5f, -0.69631064f);
		// camera.lookAt(0, 0, 0);
		// camera.zoom = 1f;
		camera.near = 0.01f;
		camera.update();
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		fpsCounterComponent.update(delta);
		
		controller.update();
		updateCameraViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		updateCamera();
		
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		if (sceneContext.isLightsNeedUpdating()) sceneContext.updateLights();
		
		tileRendererMap.renderAll(camera);
		entityRendererMap.renderAll(camera);
		drawDebugBatch();
		fpsCounterComponent.render(delta);
	}
	
	private void drawDebugBatch() {
		debugBatch.begin();
		drawProfilerInfo();
		debugBatch.end();
	}
	
	private void drawProfilerInfo() {
		int tileBatches = tileRendererMap.getObjectRendererMap().size();
		int tileVoxels = tileRendererMap.getVoxelCount();
		
		int entityBatches = entityRendererMap.getObjectRendererMap().size();
		int entityVoxels = entityRendererMap.getVoxelCount();
		
		debugFont.draw(debugBatch, String.format(
			"Tile batches: %,d  Tile voxels: %,d  Entity batches: %,d  Entity voxels: %,d \n" +
			"Total batches: %,d  Total voxels: %,d  Lights: %,d\n" +
			"Camera pos: %f %f %f",
			tileBatches,
			tileVoxels,
			entityBatches,
			entityVoxels,
			tileBatches + entityBatches,
			tileVoxels + entityVoxels,
			sceneContext.getLights().size(),
			camera.position.x, camera.position.y, camera.position.z
		), 16, 48);
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		updateCameraViewport(width, height);
		fpsCounterComponent.resize(width, height);
	}
}
