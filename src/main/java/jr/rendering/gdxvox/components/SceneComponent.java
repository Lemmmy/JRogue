package jr.rendering.gdxvox.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import jr.ErrorHandler;
import jr.JRogue;
import jr.rendering.base.components.RendererComponent;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.gdxvox.screens.VoxGameScreen;
import jr.rendering.utils.ShaderLoader;
import jr.rendering.utils.TimeProfiler;
import lombok.Getter;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import java.lang.reflect.Field;

@Getter
public class SceneComponent extends RendererComponent<VoxGameScreen> {
	private SceneContext sceneContext;
	
	private CameraInputController cameraController;
	
	private RenderContext renderContext;
	private TextureBinder textureBinder;
	
	private ModelBuilder modelBuilder = new ModelBuilder();
	private ModelBatch fullscreenQuadBatch;
	private Model fullscreenQuadModel;
	private ModelInstance fullscreenQuad;
	
	private int fullscreenQuadShaderHandle = -1, uniformBlockIndex = -1;
	private ShaderProgram fullscreenQuadShader;
	
	private ShaderProgram fxaaShader;
	private FrameBuffer fxaaFramebuffer;
	
	public SceneComponent(VoxGameScreen voxGameScreen, SceneContext sceneContext) {
		super(voxGameScreen);
		
		this.sceneContext = sceneContext;
	}
	
	@Override
	public boolean useMainBatch() {
		return true;
	}
	
	public void initialiseFullscreenQuad() {
		if (fullscreenQuadModel != null) {
			fullscreenQuadModel.dispose();
		}
		
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		fullscreenQuadModel = modelBuilder.createRect(
			0f, height, 0f,
			width, height, 0f,
			width, 0f, 0f,
			0f, 0f, 0f,
			0f, 0f, -1f,
			GL20.GL_TRIANGLES,
			new Material(ColorAttribute.createDiffuse(Color.WHITE)),
			VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates
		);
		
		fullscreenQuad = new ModelInstance(fullscreenQuadModel);
	}
	
	@Override
	public void initialise() {
		textureBinder = new DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN);
		renderContext = new RenderContext(textureBinder);
		
		cameraController = new CameraInputController(sceneContext.sceneCamera);
		
		fullscreenQuadBatch = new ModelBatch();
		fullscreenQuadShader = ShaderLoader.getProgram("shaders/fullscreen_quad");
		
		initialiseFullscreenQuad();
		
		if (settings.isUseFxaa()) initialiseFXAA();
		
		try {
			Field programField = ShaderProgram.class.getDeclaredField("program");
			programField.setAccessible(true);
			fullscreenQuadShaderHandle = (int) programField.get(fullscreenQuadShader);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			ErrorHandler.error("Unable to get fullscreen quad shader program handle via reflection", e);
		}
	}
	
	private void initialiseFXAA() {
		fxaaShader = ShaderLoader.getProgram("shaders/fxaa");
		
		initialiseFXAAFrameBuffer();
	}
	
	private void initialiseFXAAFrameBuffer() {
		if (fxaaFramebuffer != null) fxaaFramebuffer.dispose();
		
		fxaaFramebuffer = FrameBuffer.createFrameBuffer(
			Pixmap.Format.RGBA8888,
			Gdx.graphics.getWidth(),
			Gdx.graphics.getHeight(),
			false,
			false
		);
	}
	
	@Override
	public void render(float dt) {
		FrameBuffer fb = sceneContext.gBuffersContext.getFrameBuffer();
		Array<Texture> textures = fb.getTextureAttachments();
		
		// draw the scene to the FBO
		renderScene(fb);
		
		// draw the FBO to the screen
		if (settings.isUseFxaa()) fxaaFramebuffer.begin();
		renderFBO(textures);
		if (settings.isUseFxaa()) fxaaFramebuffer.end();
		
		renderFXAA();
	}
	
	private void renderScene(FrameBuffer fb) {
		fb.begin();
		
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT | Gdx.gl.GL_DEPTH_BUFFER_BIT);
		
		sceneContext.renderAllMaps(RenderPass.MAIN_PASS);
		
		fb.end();
	}
	
	private void renderFBO(Array<Texture> textures) {
		TimeProfiler.begin("[P_PURPLE_1]SceneComponent.renderFBO[]");
		
		renderContext.begin();
		
		fullscreenQuadShader.begin();
		fullscreenQuadShader.setUniformMatrix("u_projTrans", renderer.getScreenCamera().combined);
		fullscreenQuadShader.setUniformi("u_g_diffuse", textureBinder.bind(textures.get(0)));
		fullscreenQuadShader.setUniformi("u_g_normal", textureBinder.bind(textures.get(1)));
		fullscreenQuadShader.setUniformi("u_g_pos", textureBinder.bind(textures.get(2)));
		fullscreenQuadShader.setUniformi("u_g_depth", textureBinder.bind(textures.get(3)));
		
		Color ambientColour = sceneContext.lightContext.getAmbientLight();
		fullscreenQuadShader.setUniformf("u_ambientLight", ambientColour.r, ambientColour.g, ambientColour.b);
		
		if (uniformBlockIndex == -1) {
			uniformBlockIndex = GL31.glGetUniformBlockIndex(fullscreenQuadShaderHandle, "Lights");
			
			if (uniformBlockIndex == -1) {
				ErrorHandler.error("Uniform block index -1", new RuntimeException("Uniform block index -1"));
				GL30.glBindVertexArray(0);
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
		
		fullscreenQuadModel.meshes.get(0).render(fullscreenQuadShader, GL20.GL_TRIANGLES);
		
		fullscreenQuadShader.end();
		
		renderContext.end();
		
		TimeProfiler.end("[P_PURPLE_1]SceneComponent.renderFBO[]");
	}
	
	private void renderFXAA() {
		TimeProfiler.begin("[P_PURPLE_1]SceneComponent.renderFXAA[]");
		
		if (settings.isUseFxaa()) {
			renderContext.begin();
			
			fxaaShader.begin();
			fxaaShader.setUniformMatrix("u_projTrans", renderer.getScreenCamera().combined);
			fxaaShader.setUniformi("u_texture0", textureBinder.bind(fxaaFramebuffer.getColorBufferTexture()));
			fxaaShader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			
			fullscreenQuadModel.meshes.get(0).render(fxaaShader, GL20.GL_TRIANGLES);
			
			fxaaShader.end();
			
			renderContext.end();
		}
		
		TimeProfiler.end("[P_PURPLE_1]SceneComponent.renderFXAA[]");
	}
	
	@Override
	public void update(float dt) {
		cameraController.update();
		sceneContext.update(dt);
	}
	
	@Override
	public void resize(int width, int height) {
		try {
			initialiseFullscreenQuad();
		
			if (settings.isUseFxaa()) initialiseFXAAFrameBuffer();
			
			sceneContext.resize(width, height);
		} catch (IllegalStateException e) {
			JRogue.getLogger().error("Resize error", e);
		}
	}
	
	@Override
	public void dispose() {
		if (fullscreenQuadModel != null) {
			fullscreenQuadModel.dispose();
		}
		
		fullscreenQuadBatch.dispose();
		
		sceneContext.dispose();
	}
}
