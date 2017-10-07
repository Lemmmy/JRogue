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
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import jr.JRogue;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.events.EventListener;
import jr.rendering.gdx2d.GameAdapter;
import jr.rendering.gdx2d.components.FPSCounterComponent;
import jr.rendering.gdx2d.screens.BasicScreen;
import jr.rendering.gdx2d.utils.FontLoader;
import jr.rendering.gdx2d.utils.ShaderLoader;
import jr.rendering.gdxvox.objects.tiles.TileRendererMap;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

import java.nio.Buffer;

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
	
	private static final float[] VOXEL_VERTICES = new float[] {
		0.5f, -0.5f, -0.5f, 1, 1, 0,
		-0.5f, -0.5f, 0.5f, 0, 1, 0,
		-0.5f, -0.5f, -0.5f, 1, 0, 0,
		0.5f, -0.5f, -0.5f, 1, 1, 0,
		0.5f, -0.5f, 0.5f, 0, 0, 1,
		-0.5f, -0.5f, 0.5f, 0, 1, 0,
		-0.5f, 0.5f, -0.5f, 1, 0, 1,
		-0.5f, -0.5f, -0.5f, 1, 0, 0,
		-0.5f, -0.5f, 0.5f, 0, 1, 0,
		-0.5f, 0.5f, 0.5f, 0, 1, 1,
		-0.5f, 0.5f, -0.5f, 1, 0, 1,
		-0.5f, -0.5f, 0.5f, 0, 1, 0,
		-0.5f, 0.5f, 0.5f, 0, 1, 1,
		-0.5f, -0.5f, 0.5f, 0, 1, 0,
		0.5f, 0.5f, 0.5f, 1, 1, 1,
		0.5f, 0.5f, 0.5f, 1, 1, 1,
		-0.5f, -0.5f, 0.5f, 0, 1, 0,
		0.5f, -0.5f, 0.5f, 0, 0, 1,
		0.5f, 0.5f, 0.5f, 1, 1, 1,
		0.5f, -0.5f, 0.5f, 0, 0, 1,
		0.5f, -0.5f, -0.5f, 1, 1, 0,
		0.5f, 0.5f, -0.5f, 0, 0, 0,
		0.5f, -0.5f, -0.5f, 1, 1, 0,
		-0.5f, 0.5f, -0.5f, 1, 0, 1,
		-0.5f, 0.5f, -0.5f, 1, 0, 1,
		0.5f, -0.5f, -0.5f, 1, 1, 0,
		-0.5f, -0.5f, -0.5f, 1, 0, 0,
		-0.5f, 0.5f, -0.5f, 1, 0, 1,
		-0.5f, 0.5f, 0.5f, 0, 1, 1,
		0.5f, 0.5f, -0.5f, 0, 0, 0,
		-0.5f, 0.5f, 0.5f, 0, 1, 1,
		0.5f, 0.5f, 0.5f, 1, 1, 1,
		0.5f, 0.5f, -0.5f, 0, 0, 0,
		0.5f, -0.5f, -0.5f, 1, 1, 0,
		0.5f, 0.5f, -0.5f, 0, 0, 0,
		0.5f, 0.5f, 0.5f, 1, 1, 1,
	};
	
	private int voxelBuffer, voxelVAO, voxelInstanceBuffer;
	
	private ShaderProgram voxelShader;
	
	public static final float[] VOXELS = new float[]{
		0, 0, 0,
		1, 0, 0,
		2, 0, 0,
		1, 1, 0,
		1, 2, 0
	};
	
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
		
		DefaultShader.defaultCullFace = GL11.GL_BACK;
		
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
		
		Buffer voxelFloatBuffer = BufferUtils.createFloatBuffer(VOXEL_VERTICES.length)
			.put(VOXEL_VERTICES).flip();
		
		voxelBuffer = Gdx.gl.glGenBuffer();
		voxelInstanceBuffer = Gdx.gl.glGenBuffer();
		
		Buffer voxelsFloatBuffer = BufferUtils.createFloatBuffer(VOXELS.length)
			.put(VOXELS).flip();
		
		voxelVAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(voxelVAO);
		
		// voxel cube buffer
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, voxelBuffer);
		Gdx.gl.glBufferData(Gdx.gl.GL_ARRAY_BUFFER, VOXEL_VERTICES.length * 4, voxelFloatBuffer, Gdx.gl.GL_STATIC_DRAW);
		
		Gdx.gl.glEnableVertexAttribArray(0);
		Gdx.gl.glVertexAttribPointer(0, 3, Gdx.gl.GL_FLOAT, false, 6 * 4, 0);
		Gdx.gl.glEnableVertexAttribArray(1);
		Gdx.gl.glVertexAttribPointer(1, 3, Gdx.gl.GL_FLOAT, false, 6 * 4, 3 * 4);
		
		// instance buffer
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, voxelInstanceBuffer);
		Gdx.gl.glBufferData(Gdx.gl.GL_ARRAY_BUFFER, VOXELS.length * 4, voxelsFloatBuffer, Gdx.gl.GL_STATIC_DRAW);
		Gdx.gl.glEnableVertexAttribArray(2);
		Gdx.gl.glVertexAttribPointer(2, 3, Gdx.gl.GL_FLOAT, false, 3 * 4, 0);
		GL33.glVertexAttribDivisor(2, 1);
		
		GL30.glBindVertexArray(0);
		
		voxelShader = ShaderLoader.getProgram("shaders/voxel");
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
		
		voxelShader.begin();
		voxelShader.setUniformMatrix("u_projTrans", camera.combined);
		
		Gdx.gl.glEnable(Gdx.gl.GL_DEPTH_TEST);
		Gdx.gl.glEnable(Gdx.gl.GL_CULL_FACE);
		Gdx.gl.glCullFace(Gdx.gl.GL_BACK);
		
		GL30.glBindVertexArray(voxelVAO);
		GL31.glDrawArraysInstanced(Gdx.gl.GL_TRIANGLES, 0, VOXEL_VERTICES.length, VOXELS.length);
		GL30.glBindVertexArray(0);
		
		Gdx.gl.glDisable(Gdx.gl.GL_CULL_FACE);
		Gdx.gl.glDisable(Gdx.gl.GL_DEPTH_TEST);
		
		voxelShader.end();
		
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
