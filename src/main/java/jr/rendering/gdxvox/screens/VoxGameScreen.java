package jr.rendering.gdxvox.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import jr.JRogue;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.events.EventListener;
import jr.rendering.gdx2d.GameAdapter;
import jr.rendering.gdx2d.components.FPSCounterComponent;
import jr.rendering.gdx2d.screens.BasicScreen;
import jr.rendering.gdx2d.utils.FontLoader;
import jr.rendering.gdxvox.models.magicavoxel.ModelConverter;
import jr.rendering.gdxvox.objects.tiles.TileRendererMap;
import lombok.val;

import java.util.List;

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
	
	private TileRendererMap tileRendererMap;
	// private OrthographicCamera camera;
	private PerspectiveCamera camera;
	private CameraInputController controller;
	
	private GLProfiler profiler;
	
	private ModelBatch modelBatch;
	private SpriteBatch debugBatch;
	
	private BitmapFont debugFont;
	
	private FPSCounterComponent fpsCounterComponent;
	
	private Environment environment;
	
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
		profiler = new GLProfiler(Gdx.graphics);
		profiler.enable();
		
		tileRendererMap = new TileRendererMap();
		tileRendererMap.initialise();
		dungeon.eventSystem.addListener(tileRendererMap);
		
		// camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		updateCameraViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		controller = new CameraInputController(camera);
		addInputProcessor(controller);
		
		modelBatch = new ModelBatch();
		debugBatch = new SpriteBatch();
		
		debugFont = FontLoader.getFont("fonts/Lato-Regular.ttf", 12, true, false);
		
		fpsCounterComponent = new FPSCounterComponent(null, dungeon, settings);
		fpsCounterComponent.initialise();
		
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
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
		// camera.near = 0.001f;
		// camera.far = 6000f;
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
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT |
			(Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
		
		modelBatch.begin(camera);
		tileRendererMap.renderAll(modelBatch);
		modelBatch.end();
		
		drawDebugBatch();
		fpsCounterComponent.render(delta);
	}
	
	private void drawDebugBatch() {
		debugBatch.begin();
		drawProfilerInfo();
		debugBatch.end();
	}
	
	private void drawProfilerInfo() {
		debugFont.draw(debugBatch, String.format(
			"Draw calls: %,d   Calls: %,d   Shader Switches: %,d   Texture Bindings: %,d   Vertex Count: %,f",
			profiler.getDrawCalls(),
			profiler.getCalls(),
			profiler.getShaderSwitches(),
			profiler.getTextureBindings(),
			profiler.getVertexCount().total
		), 16, 16);
		
		profiler.reset();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		updateCameraViewport(width, height);
		fpsCounterComponent.resize(width, height);
	}
}
