package jr.rendering.gdxvox.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import jr.ErrorHandler;
import jr.JRogue;
import jr.Settings;
import jr.dungeon.Dungeon;
import jr.dungeon.events.EventListener;
import jr.rendering.base.components.FPSCounterComponent;
import jr.rendering.base.screens.BasicScreen;
import jr.rendering.gdx2d.GameAdapter;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.gdxvox.objects.entities.EntityRendererMap;
import jr.rendering.gdxvox.objects.tiles.TileRendererMap;
import jr.rendering.gdxvox.primitives.FullscreenQuad;
import jr.rendering.gdxvox.primitives.VoxelCube;
import jr.rendering.utils.FontLoader;
import jr.rendering.utils.ShaderLoader;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLUtil;

import java.awt.*;
import java.lang.reflect.Field;

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
	
	private OrthographicCamera screenCamera;
	
	// private OrthographicCamera camera;
	private PerspectiveCamera camera;
	private CameraInputController controller;
	
	private SpriteBatch debugBatch;
	private BitmapFont debugFont;
	private FPSCounterComponent fpsCounterComponent;
	
	private Dimension fullscreenDimension;
	private int fullscreenQuadVAO, fullscreenQuadShaderHandle = -1, uniformBlockIndex = -1;
	private ShaderProgram fullscreenQuadShader;
	
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
		GLUtil.setupDebugMessageCallback(System.out);
		
		sceneContext = new SceneContext(dungeon);
		
		tileRendererMap = new TileRendererMap(sceneContext);
		tileRendererMap.initialise();
		dungeon.eventSystem.addListener(tileRendererMap);
		
		entityRendererMap = new EntityRendererMap(sceneContext);
		entityRendererMap.initialise();
		dungeon.eventSystem.addListener(entityRendererMap);
		
		screenCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		// camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		updateCameraViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.lookAt(20, 1, 20);
		
		controller = new CameraInputController(camera);
		addInputProcessor(controller);
		
		debugBatch = new SpriteBatch();
		debugFont = FontLoader.getFont("fonts/Lato-Regular.ttf", 12, true, false);
		
		fpsCounterComponent = new FPSCounterComponent(null);
		fpsCounterComponent.initialise();
		
		fullscreenDimension = new Dimension(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		fullscreenQuadVAO = FullscreenQuad.getVAO(fullscreenDimension);
		fullscreenQuadShader = ShaderLoader.getProgram("shaders/fullscreen_quad");
		
		try {
			Field programField = ShaderProgram.class.getDeclaredField("program");
			programField.setAccessible(true);
			fullscreenQuadShaderHandle = (int) programField.get(fullscreenQuadShader);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			ErrorHandler.error("Unable to get fullscreen quad shader program handle via reflection", e);
		}
		
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
		camera.near = 0.1f;
		camera.update();
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		fpsCounterComponent.update(delta);
		
		screenCamera.update();
		
		controller.update();
		updateCameraViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		updateCamera();
		
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		Gdx.gl.glBindFramebuffer(Gdx.gl.GL_FRAMEBUFFER, sceneContext.gBuffersContext.getGBuffersHandle());
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT | Gdx.gl.GL_DEPTH_BUFFER_BIT);
		
		sceneContext.update();
		
		tileRendererMap.renderAll(camera);
		entityRendererMap.renderAll(camera);
		
		Gdx.gl.glBindFramebuffer(Gdx.gl.GL_FRAMEBUFFER, 0);
		
		GL30.glBindVertexArray(fullscreenQuadVAO);
		
		sceneContext.gBuffersContext.bindTextures();
		
		fullscreenQuadShader.begin();
		fullscreenQuadShader.setUniformMatrix("u_projTrans", screenCamera.combined);
		fullscreenQuadShader.setUniformi("u_g_diffuse", 0);
		fullscreenQuadShader.setUniformi("u_g_normal", 1);
		fullscreenQuadShader.setUniformi("u_g_pos", 2);
		// fullscreenQuadShader.setUniformi("u_g_depth", 3);
		
		if (uniformBlockIndex == -1) {
			uniformBlockIndex = GL31.glGetUniformBlockIndex(fullscreenQuadShaderHandle, "Lights");
			
			if (uniformBlockIndex == -1) {
				ErrorHandler.error("Uniform block index -1", new RuntimeException("Uniform block index -1"));
				return;
			}
		}
		
		GL30.glBindBufferRange(
			GL31.GL_UNIFORM_BUFFER,
			uniformBlockIndex,
			sceneContext.lightContext.getLightBufferHandle(),
			0,
			sceneContext.lightContext.getBufferSize()
		);
		Gdx.gl.glDrawArrays(Gdx.gl.GL_TRIANGLE_STRIP, 0, FullscreenQuad.QUAD_ELEMENT_COUNT);
		
		fullscreenQuadShader.end();
		
		GL30.glBindVertexArray(0);
		
		Gdx.gl.glActiveTexture(Gdx.gl.GL_TEXTURE0);
		
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
			sceneContext.lightContext.getLights().size(),
			camera.position.x, camera.position.y, camera.position.z
		), 16, 48);
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		if (fullscreenDimension != null) {
			FullscreenQuad.dispose(fullscreenDimension);
		}
		
		fullscreenDimension = new Dimension(width, height);
		fullscreenQuadVAO = FullscreenQuad.getVAO(fullscreenDimension);
		
		screenCamera.setToOrtho(false, width, height);
		updateCameraViewport(width, height);
		sceneContext.resize(width, height);
		
		fpsCounterComponent.resize(width, height);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		if (fullscreenDimension != null) {
			FullscreenQuad.dispose(fullscreenDimension);
			fullscreenQuadVAO = -1;
		}
		
		VoxelCube.dispose();
	}
}
