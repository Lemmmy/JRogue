package jr.rendering.gdxvox.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
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
	private OrthographicCamera camera;
	
	private GLProfiler profiler;
	
	private ModelBatch modelBatch;
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
		profiler = new GLProfiler(Gdx.graphics);
		profiler.enable();
		
		tileRendererMap = new TileRendererMap();
		tileRendererMap.initialise();
		dungeon.eventSystem.addListener(tileRendererMap);
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		updateCameraViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		modelBatch = new ModelBatch();
		debugBatch = new SpriteBatch();
		
		debugFont = FontLoader.getFont("fonts/Lato-Regular.ttf", 12, true, false);
		
		fpsCounterComponent = new FPSCounterComponent(null, dungeon, settings);
		fpsCounterComponent.initialise();
		
		ModelConverter.test();
	}
	
	private void updateCameraViewport(float width, float height) {
		float aspectRatio = width / height;
		camera.setToOrtho(false, VIEWPORT_SIZE * aspectRatio, VIEWPORT_SIZE);
	}
	
	private void updateCamera() {
		camera.position.set(40f, 10f, 40f);
		camera.direction.set(-0.69631064f, -0.5f, -0.69631064f);
		camera.zoom = 1f;
		camera.near = 0.001f;
		camera.far = 6000f;
		camera.update();
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		fpsCounterComponent.update(delta);
		
		updateCameraViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		updateCamera();
		
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
			profiler.getVertexCount().latest
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
