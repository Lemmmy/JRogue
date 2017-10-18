package jr.rendering.gdxvox.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import jr.ErrorHandler;
import jr.rendering.base.components.RendererComponent;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.gdxvox.primitives.FullscreenQuad;
import jr.rendering.gdxvox.screens.VoxGameScreen;
import jr.rendering.utils.ShaderLoader;
import lombok.Getter;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import java.awt.*;
import java.lang.reflect.Field;

@Getter
public class SceneComponent extends RendererComponent<VoxGameScreen> {
	private SceneContext sceneContext;
	
	private CameraInputController cameraController;
	
	private Dimension fullscreenDimension;
	private int fullscreenQuadVAO, fullscreenQuadShaderHandle = -1, uniformBlockIndex = -1;
	private ShaderProgram fullscreenQuadShader;
	
	public SceneComponent(VoxGameScreen voxGameScreen, SceneContext sceneContext) {
		super(voxGameScreen);
		
		this.sceneContext = sceneContext;
	}
	
	@Override
	public boolean useMainBatch() {
		return true;
	}
	
	@Override
	public void initialise() {
		cameraController = new CameraInputController(sceneContext.sceneCamera);
		
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
	}
	
	@Override
	public void render(float dt) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		Gdx.gl.glBindFramebuffer(Gdx.gl.GL_FRAMEBUFFER, sceneContext.gBuffersContext.getGBuffersHandle());
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT | Gdx.gl.GL_DEPTH_BUFFER_BIT);
		
		sceneContext.renderAllMaps();
		
		Gdx.gl.glBindFramebuffer(Gdx.gl.GL_FRAMEBUFFER, 0);
		
		GL30.glBindVertexArray(fullscreenQuadVAO);
		
		sceneContext.gBuffersContext.bindTextures();
		
		fullscreenQuadShader.begin();
		fullscreenQuadShader.setUniformMatrix("u_projTrans", renderer.getScreenCamera().combined);
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
	}
	
	@Override
	public void update(float dt) {
		cameraController.update();
		sceneContext.update();
	}
	
	@Override
	public void resize(int width, int height) {
		if (fullscreenDimension != null) {
			FullscreenQuad.dispose(fullscreenDimension);
		}
		
		fullscreenDimension = new Dimension(width, height);
		fullscreenQuadVAO = FullscreenQuad.getVAO(fullscreenDimension);
		
		sceneContext.resize(width, height);
	}
	
	@Override
	public void dispose() {
		if (fullscreenDimension != null) {
			FullscreenQuad.dispose(fullscreenDimension);
			fullscreenQuadVAO = -1;
		}
	}
}
