package jr.rendering.gdxvox.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import jr.ErrorHandler;
import jr.rendering.gdxvox.context.GBuffersContext;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.gdxvox.primitives.VoxelCube;
import jr.rendering.utils.ShaderLoader;
import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public abstract class VoxelBatch<ObjectV> {
	public static final int INSTANCE_ELEMENT_COUNT = 6;
	public static final int INSTANCE_ELEMENT_SIZE = 6 * 4;
	
	private int voxelVAO, voxelInstanceBuffer = -1;
	private int instanceCount = 0;
	private ShaderProgram voxelShader;
	
	private String rendererName;
	
	private List<VoxelModelInstance> instances = new ArrayList<>();
	
	private boolean needsRebuild;
	
	public VoxelBatch(String rendererName) {
		this.rendererName = rendererName;
	}
	
	public VoxelBatch(Class<? extends AbstractObjectRenderer> rendererClass) {
		this.rendererName = rendererClass.getSimpleName();
	}
	
	private void initialiseShader() {
		voxelShader = ShaderLoader.getProgram("shaders/voxel");
	}
	
	private void initialiseVAO(FloatBuffer voxelsBuffer) {
		ErrorHandler.glErrorCheck("VoxelBatch.initialiseVAO");
		voxelInstanceBuffer = Gdx.gl.glGenBuffer();
		
		ErrorHandler.glErrorCheck("before glGenVertexArrays");
		voxelVAO = GL30.glGenVertexArrays();
		ErrorHandler.glErrorCheck("before glBindVertexArray");
		GL30.glBindVertexArray(voxelVAO);
		
		ErrorHandler.glErrorCheck("before glBindBuffer (cube VBO)");
		// voxel cube buffer
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, VoxelCube.getVBO());
		// position
		Gdx.gl.glEnableVertexAttribArray(0);
		Gdx.gl.glVertexAttribPointer(0, 3, Gdx.gl.GL_FLOAT, false, VoxelCube.CUBE_ELEMENT_SIZE, 0);
		// normal
		Gdx.gl.glEnableVertexAttribArray(1);
		Gdx.gl.glVertexAttribPointer(1, 3, Gdx.gl.GL_FLOAT, false, VoxelCube.CUBE_ELEMENT_SIZE, 3 * 4);
		
		ErrorHandler.glErrorCheck("before glBindBuffer (voxel instances)");
		// instance buffer
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, voxelInstanceBuffer);
		System.err.println(String.format("[[V]]%s|%d|%d", rendererName, voxelsBuffer.capacity(), voxelsBuffer.capacity() * 4));
		Gdx.gl.glBufferData(Gdx.gl.GL_ARRAY_BUFFER, voxelsBuffer.capacity() * 4, voxelsBuffer, Gdx.gl.GL_STATIC_DRAW);
		// instance position
		Gdx.gl.glEnableVertexAttribArray(2);
		Gdx.gl.glVertexAttribPointer(2, 3, Gdx.gl.GL_FLOAT, false, INSTANCE_ELEMENT_SIZE, 0);
		GL33.glVertexAttribDivisor(2, 1);
		// instance colour
		Gdx.gl.glEnableVertexAttribArray(3);
		Gdx.gl.glVertexAttribPointer(3, 3, Gdx.gl.GL_FLOAT, false, INSTANCE_ELEMENT_SIZE, 3 * 4);
		GL33.glVertexAttribDivisor(3, 1);
		
		ErrorHandler.glErrorCheck("before unbind all");
		
		GL30.glBindVertexArray(0);
		
		Gdx.gl.glDisableVertexAttribArray(0);
		Gdx.gl.glDisableVertexAttribArray(1);
		Gdx.gl.glDisableVertexAttribArray(2);
		Gdx.gl.glDisableVertexAttribArray(3);
	}
	
	public boolean contains(ObjectV object) {
		return instances.stream().anyMatch(instance -> instance.getObject() == object);
	}
	
	public boolean contains(String instanceID) {
		return instances.stream().anyMatch(instance -> instance.getInstanceID().equals(instanceID));
	}
	
	public boolean contains(VoxelModelInstance model) {
		return instances.contains(model);
	}
	
	public void add(ObjectV object, VoxelModelInstance model) {
		model.setObject(object);
		if (model.getInstanceID() == null) model.setInstanceID(String.valueOf(System.identityHashCode(object)));
		updateObjectPosition(object, model);
		
		instances.add(model);
		needsRebuild = true;
	}
	
	protected abstract void updateObjectPosition(ObjectV object, VoxelModelInstance model);
	
	public void remove(ObjectV object) {
		instances.removeIf(instance -> instance.getObject() == object);
		needsRebuild = true;
	}
	
	public void remove(String instanceID) {
		instances.removeIf(instance -> instance.getInstanceID().equals(instanceID));
		needsRebuild = true;
	}
	
	public void remove(VoxelModelInstance instance) {
		instances.remove(instance);
		needsRebuild = true;
	}
	
	public void move(ObjectV object) {
		instances.stream()
			.filter(i -> i.getObject().equals(object))
			.forEach(i -> updateObjectPosition(object, i));
	}
	
	public void move(ObjectV object, float x, float y, float z) {
		instances.stream()
			.filter(i -> i.getObject().equals(object))
			.forEach(i -> i.setPosition(x, y, z));
	}
	
	public Optional<VoxelModelInstance> getInstance(ObjectV object) {
		return instances.stream()
			.filter(i -> i.getObject().equals(object))
			.findFirst();
	}
	
	public void clear() {
		instances.clear();
		needsRebuild = true;
	}
	
	public void rebuildVoxels(SceneContext scene) {
		if (voxelShader == null) initialiseShader();
		
		// keep track of the locations of all buffers so we can update them later
		AtomicInteger location = new AtomicInteger(0);
		
		List<FloatBuffer> voxelBuffers = instances.stream()
			.map(VoxelModelInstance::compileVoxels)
			.collect(Collectors.toList());
		
		int size = voxelBuffers.stream()
			.mapToInt(Buffer::capacity)
			.sum();
		
		FloatBuffer compiledBuffer = BufferUtils.createFloatBuffer(size);
		instances.forEach(instance -> {
			FloatBuffer instanceBuffer = instance.getCompiledVoxels();
			compiledBuffer.put(instanceBuffer);
			instance.setBufferLocation(location.getAndAdd(instanceBuffer.capacity()));
		});
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
			ErrorHandler.glErrorCheck("after all rebuild");
		} else {
			Stream instanceStream = instances.stream()
				.filter(VoxelModelInstance::isUpdated);
			
			if (instanceStream.count() > 0) {
				Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, voxelInstanceBuffer);
				ByteBuffer mappedBuffer = GL15.glMapBuffer(Gdx.gl.GL_ARRAY_BUFFER, GL15.GL_WRITE_ONLY);
				
				instances.stream()
					.filter(VoxelModelInstance::isUpdated)
					.forEach(instance -> {
						int start = instance.getBufferLocation();
						FloatBuffer instanceBuffer = instance.compileVoxels();
						
						for (int i = 0; i < instanceBuffer.capacity(); i++) {
							mappedBuffer.putFloat((start + i) * 4, instanceBuffer.get(i));
						}
					});
				
				GL15.glUnmapBuffer(Gdx.gl.GL_ARRAY_BUFFER);
				Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, 0);
			}
		}
		
		instances.forEach(i -> i.setUpdated(false));
		
		ErrorHandler.glErrorCheck("before glViewport");
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		voxelShader.begin();
		voxelShader.setUniformMatrix("u_projTrans", camera.combined);
		
		Gdx.gl.glEnable(Gdx.gl.GL_DEPTH_TEST);
		Gdx.gl.glEnable(Gdx.gl.GL_CULL_FACE);
		Gdx.gl.glCullFace(Gdx.gl.GL_BACK);
		
		ErrorHandler.glErrorCheck("before glBindVertexArray");
		GL30.glBindVertexArray(voxelVAO);
		
		ErrorHandler.glErrorCheck("before glDrawBuffers");
		GL20.glDrawBuffers(GBuffersContext.G_BUFFERS_ATTACHMENTS);
		ErrorHandler.glErrorCheck("before glDrawArraysInstanced");
		GL31.glDrawArraysInstanced(
			Gdx.gl.GL_TRIANGLES,
			0,
			VoxelCube.CUBE_VERTICES.length / VoxelCube.CUBE_ELEMENT_COUNT,
			instanceCount
		);
		ErrorHandler.glErrorCheck("after glDrawArraysInstanced");
		GL30.glBindVertexArray(0);
		
		Gdx.gl.glDisable(Gdx.gl.GL_CULL_FACE);
		Gdx.gl.glDisable(Gdx.gl.GL_DEPTH_TEST);
		
		voxelShader.end();
	}
}
