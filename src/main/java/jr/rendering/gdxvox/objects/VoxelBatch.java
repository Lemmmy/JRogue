package jr.rendering.gdxvox.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import jr.ErrorHandler;
import jr.JRogue;
import jr.rendering.gdx2d.utils.ShaderLoader;
import jr.rendering.gdxvox.utils.Light;
import jr.rendering.gdxvox.utils.SceneContext;
import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static jr.rendering.gdxvox.utils.SceneContext.MAX_LIGHTS;

@Getter
public abstract class VoxelBatch<ObjectV> {
	public static final int INSTANCE_ELEMENT_COUNT = 6;
	public static final int INSTANCE_ELEMENT_SIZE = 6 * 4;
	
	public static final int CUBE_ELEMENT_COUNT = 6;
	public static final int CUBE_ELEMENT_SIZE = 6 * 4;
	
	private static final float[] CUBE_VERTICES = new float[] {
		-0.03125f, 0.03125f, 0.03125f, -1f, 0f, 0f,
		-0.03125f, -0.03125f, -0.03125f, -1f, 0f, 0f,
		-0.03125f, -0.03125f, 0.03125f, -1f, 0f, 0f,
		-0.03125f, 0.03125f, -0.03125f, 0f, 0f, -1f,
		0.03125f, -0.03125f, -0.03125f, 0f, 0f, -1f,
		-0.03125f, -0.03125f, -0.03125f, 0f, 0f, -1f,
		0.03125f, 0.03125f, -0.03125f, 1f, 0f, 0f,
		0.03125f, -0.03125f, 0.03125f, 1f, 0f, 0f,
		0.03125f, -0.03125f, -0.03125f, 1f, 0f, 0f,
		0.03125f, 0.03125f, 0.03125f, 0f, 0f, 1f,
		-0.03125f, -0.03125f, 0.03125f, 0f, 0f, 1f,
		0.03125f, -0.03125f, 0.03125f, 0f, 0f, 1f,
		0.03125f, -0.03125f, -0.03125f, 0f, -1f, 0f,
		-0.03125f, -0.03125f, 0.03125f, 0f, -1f, 0f,
		-0.03125f, -0.03125f, -0.03125f, 0f, -1f, 0f,
		-0.03125f, 0.03125f, -0.03125f, 0f, 1f, 0f,
		0.03125f, 0.03125f, 0.03125f, 0f, 1f, 0f,
		0.03125f, 0.03125f, -0.03125f, 0f, 1f, 0f,
		-0.03125f, 0.03125f, 0.03125f, -1f, 0f, 0f,
		-0.03125f, 0.03125f, -0.03125f, -1f, 0f, 0f,
		-0.03125f, -0.03125f, -0.03125f, -1f, 0f, 0f,
		-0.03125f, 0.03125f, -0.03125f, 0f, 0f, -1f,
		0.03125f, 0.03125f, -0.03125f, 0f, 0f, -1f,
		0.03125f, -0.03125f, -0.03125f, 0f, 0f, -1f,
		0.03125f, 0.03125f, -0.03125f, 1f, 0f, 0f,
		0.03125f, 0.03125f, 0.03125f, 1f, 0f, 0f,
		0.03125f, -0.03125f, 0.03125f, 1f, 0f, 0f,
		0.03125f, 0.03125f, 0.03125f, 0f, 0f, 1f,
		-0.03125f, 0.03125f, 0.03125f, 0f, 0f, 1f,
		-0.03125f, -0.03125f, 0.03125f, 0f, 0f, 1f,
		0.03125f, -0.03125f, -0.03125f, 0f, -1f, 0f,
		0.03125f, -0.03125f, 0.03125f, 0f, -1f, 0f,
		-0.03125f, -0.03125f, 0.03125f, 0f, -1f, 0f,
		-0.03125f, 0.03125f, -0.03125f, 0f, 1f, 0f,
		-0.03125f, 0.03125f, 0.03125f, 0f, 1f, 0f,
		0.03125f, 0.03125f, 0.03125f, 0f, 1f, 0f
	};
	
	private static int cubeBuffer = -1;
	
	private int voxelVAO, voxelInstanceBuffer = -1;
	private int instanceCount = 0;
	private ShaderProgram voxelShader;
	private int voxelShaderHandle = -1;
	private int uniformBlockIndex = -1;
	
	private List<VoxelModelInstance> objects = new ArrayList<>();
	
	private boolean needsRebuild;
	
	public VoxelBatch() {
		if (cubeBuffer == -1) initialiseCubeBuffer();
	}
	
	private static void initialiseCubeBuffer() {
		Buffer cubeVerticesBuffer = BufferUtils.createFloatBuffer(CUBE_VERTICES.length)
			.put(CUBE_VERTICES).flip();
		
		cubeBuffer = Gdx.gl.glGenBuffer();
		
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, cubeBuffer);
		Gdx.gl.glBufferData(Gdx.gl.GL_ARRAY_BUFFER, CUBE_VERTICES.length * 4, cubeVerticesBuffer, Gdx.gl.GL_STATIC_DRAW);
	}
	
	private void initialiseShader() {
		voxelShader = ShaderLoader.getProgram("shaders/voxel");
		
		try {
			Field voxelShaderHandleField = ShaderProgram.class.getDeclaredField("program");
			voxelShaderHandleField.setAccessible(true);
			voxelShaderHandle = (int) voxelShaderHandleField.get(voxelShader);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			ErrorHandler.error("Unable to get voxel shader program handle via reflection", e);
		}
	}
	
	private void initialiseVAO(FloatBuffer voxelsBuffer) {
		voxelInstanceBuffer = Gdx.gl.glGenBuffer();
		
		voxelVAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(voxelVAO);
		
		// voxel cube buffer
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, cubeBuffer);
		// position
		Gdx.gl.glEnableVertexAttribArray(0);
		Gdx.gl.glVertexAttribPointer(0, 3, Gdx.gl.GL_FLOAT, false, CUBE_ELEMENT_SIZE, 0);
		// normal
		Gdx.gl.glEnableVertexAttribArray(1);
		Gdx.gl.glVertexAttribPointer(1, 3, Gdx.gl.GL_FLOAT, false, CUBE_ELEMENT_SIZE, 3 * 4);
		
		// instance buffer
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, voxelInstanceBuffer);
		Gdx.gl.glBufferData(Gdx.gl.GL_ARRAY_BUFFER, voxelsBuffer.capacity() * 4, voxelsBuffer, Gdx.gl.GL_STATIC_DRAW);
		// instance position
		Gdx.gl.glEnableVertexAttribArray(2);
		Gdx.gl.glVertexAttribPointer(2, 3, Gdx.gl.GL_FLOAT, false, INSTANCE_ELEMENT_SIZE, 0);
		GL33.glVertexAttribDivisor(2, 1);
		// instance colour
		Gdx.gl.glEnableVertexAttribArray(3);
		Gdx.gl.glVertexAttribPointer(3, 3, Gdx.gl.GL_FLOAT, false, INSTANCE_ELEMENT_SIZE, 3 * 4);
		GL33.glVertexAttribDivisor(3, 1);
		
		GL30.glBindVertexArray(0);
	}
	
	public boolean contains(ObjectV object) {
		return objects.stream().anyMatch(instance -> instance.getObject() == object);
	}
	
	public boolean contains(String instanceID) {
		return objects.stream().anyMatch(instance -> instance.getInstanceID().equals(instanceID));
	}
	
	public boolean contains(VoxelModelInstance model) {
		return objects.contains(model);
	}
	
	public void add(ObjectV object, VoxelModelInstance model) {
		model.setObject(object);
		if (model.getInstanceID() == null) model.setInstanceID(String.valueOf(System.identityHashCode(object)));
		setAddedObjectPosition(object, model);
		
		objects.add(model);
		needsRebuild = true;
	}
	
	protected abstract void setAddedObjectPosition(ObjectV object, VoxelModelInstance model);
	
	public void remove(ObjectV object) {
		objects.removeIf(instance -> instance.getObject() == object);
		needsRebuild = true;
	}
	
	public void remove(String instanceID) {
		objects.removeIf(instance -> instance.getInstanceID().equals(instanceID));
		needsRebuild = true;
	}
	
	public void remove(VoxelModelInstance instance) {
		objects.remove(instance);
		needsRebuild = true;
	}
	
	public void clear() {
		objects.clear();
		needsRebuild = true;
	}
	
	public void rebuildVoxels(SceneContext scene) {
		if (voxelShader == null) initialiseShader();
		
		List<FloatBuffer> voxelBuffers = objects.stream()
			.map(VoxelModelInstance::compileVoxels)
			.collect(Collectors.toList());
		
		int size = voxelBuffers.stream()
			.mapToInt(Buffer::capacity)
			.sum();
		
		FloatBuffer compiledBuffer = BufferUtils.createFloatBuffer(size);
		voxelBuffers.forEach(compiledBuffer::put);
		compiledBuffer.flip();
		
		instanceCount = compiledBuffer.capacity() / INSTANCE_ELEMENT_COUNT;
		
		if (voxelInstanceBuffer == -1) initialiseVAO(compiledBuffer);
	}
	
	public void render(Camera camera, SceneContext scene) {
		if (voxelVAO == -1 || voxelInstanceBuffer == -1 || voxelShader == null) {
			needsRebuild = true;
		}
		
		if (needsRebuild) {
			rebuildVoxels(scene);
			needsRebuild = false;
		}
		
		voxelShader.begin();
		voxelShader.setUniformMatrix("u_projTrans", camera.combined);
		
		Gdx.gl.glEnable(Gdx.gl.GL_DEPTH_TEST);
		Gdx.gl.glEnable(Gdx.gl.GL_CULL_FACE);
		Gdx.gl.glCullFace(Gdx.gl.GL_BACK);
		
		GL30.glBindVertexArray(voxelVAO);
		
		if (uniformBlockIndex == -1) {
			uniformBlockIndex = GL31.glGetUniformBlockIndex(voxelShaderHandle, "Lights");
			
			if (uniformBlockIndex == -1) {
				ErrorHandler.error("Uniform block index -1", new RuntimeException("Uniform block index -1"));
				return;
			}
		}
		
		GL30.glBindBufferRange(
			GL31.GL_UNIFORM_BUFFER,
			uniformBlockIndex,
			scene.getLightBufferHandle(),
			0,
			16 + scene.getLights().values().stream()
				.filter(Light::isEnabled)
				.limit(MAX_LIGHTS)
				.count() * SceneContext.LIGHT_ELEMENT_SIZE
		);
		GL31.glDrawArraysInstanced(Gdx.gl.GL_TRIANGLES, 0, CUBE_VERTICES.length / CUBE_ELEMENT_COUNT, instanceCount);
		GL30.glBindVertexArray(0);
		
		Gdx.gl.glDisable(Gdx.gl.GL_CULL_FACE);
		Gdx.gl.glDisable(Gdx.gl.GL_DEPTH_TEST);
		
		voxelShader.end();
	}
}
