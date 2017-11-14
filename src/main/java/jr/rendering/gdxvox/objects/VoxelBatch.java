package jr.rendering.gdxvox.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import jr.rendering.gdxvox.components.RenderPass;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.gdxvox.lighting.Light;
import jr.rendering.gdxvox.primitives.VoxelCube;
import jr.rendering.utils.ShaderLoader;
import jr.rendering.utils.TimeProfiler;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

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
	public static final int INSTANCE_ELEMENT_COUNT = 7;
	public static final int INSTANCE_ELEMENT_SIZE = 7 * 4;
	
	private int voxelVAO, voxelInstanceBuffer = -1;
	private int instanceCount = 0;
	
	private String rendererName;
	
	private List<VoxelModelInstance> instances = new ArrayList<>();
	
	private boolean needsRebuild;
	@Setter private boolean instancesNeedRemap;
	
	@Setter private boolean isCulled = false;
	
	public VoxelBatch(String rendererName) {
		this.rendererName = rendererName;
	}
	
	public VoxelBatch(Class<?> rendererClass) {
		this.rendererName = rendererClass.getSimpleName();
	}
	
	private void initialiseVAO(ByteBuffer voxelsBuffer) {
		TimeProfiler.begin("[P_GREEN_1]VoxelBatch.initialiseVAO[]");
		voxelInstanceBuffer = Gdx.gl.glGenBuffer();
		
		voxelVAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(voxelVAO);
		
		// voxel cube buffer
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, VoxelCube.getVBO());
		// position
		Gdx.gl.glEnableVertexAttribArray(0);
		Gdx.gl.glVertexAttribPointer(0, 3, Gdx.gl.GL_FLOAT, false, VoxelCube.CUBE_ELEMENT_SIZE, 0);
		// normal
		Gdx.gl.glEnableVertexAttribArray(1);
		Gdx.gl.glVertexAttribPointer(1, 3, Gdx.gl.GL_FLOAT, false, VoxelCube.CUBE_ELEMENT_SIZE, 3 * 4);
		
		// instance buffer
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, voxelInstanceBuffer);
		Gdx.gl.glBufferData(Gdx.gl.GL_ARRAY_BUFFER, voxelsBuffer.capacity(), voxelsBuffer, Gdx.gl.GL_STATIC_DRAW);
		// instance position
		Gdx.gl.glEnableVertexAttribArray(2);
		Gdx.gl.glVertexAttribPointer(2, 3, Gdx.gl.GL_FLOAT, false, INSTANCE_ELEMENT_SIZE, 0);
		GL33.glVertexAttribDivisor(2, 1);
		// instance rotation
		Gdx.gl.glEnableVertexAttribArray(3);
		Gdx.gl.glVertexAttribPointer(3, 1, Gdx.gl.GL_FLOAT, false, INSTANCE_ELEMENT_SIZE, 3 * 4);
		GL33.glVertexAttribDivisor(3, 1);
		// instance colour
		Gdx.gl.glEnableVertexAttribArray(4);
		Gdx.gl.glVertexAttribPointer(4, 3, Gdx.gl.GL_FLOAT, false, INSTANCE_ELEMENT_SIZE, 4 * 4);
		GL33.glVertexAttribDivisor(4, 1);
		
		GL30.glBindVertexArray(0);
		
		Gdx.gl.glDisableVertexAttribArray(0);
		Gdx.gl.glDisableVertexAttribArray(1);
		Gdx.gl.glDisableVertexAttribArray(2);
		Gdx.gl.glDisableVertexAttribArray(3);
		TimeProfiler.end("[P_GREEN_1]VoxelBatch.initialiseVAO[]");
	}
	
	private void updateInstanceBuffer(ByteBuffer voxelsBuffer) {
		TimeProfiler.begin("[P_GREEN_1]VoxelBatch.updateInstanceBuffer[]");
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, voxelInstanceBuffer);
		Gdx.gl.glBufferData(Gdx.gl.GL_ARRAY_BUFFER, voxelsBuffer.capacity(), voxelsBuffer, Gdx.gl.GL_STATIC_DRAW);
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, 0);
		TimeProfiler.end("[P_GREEN_1]VoxelBatch.updateInstanceBuffer[]");
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
		model.setBatch(this);
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
		TimeProfiler.begin("[P_GREEN_1]VoxelBatch.rebuildVoxels[]");
		
		int size = 0;
		for (VoxelModelInstance voxelModelInstance : instances) {
			size += voxelModelInstance.compileVoxels().capacity();
		}
		if (size == 0) return;
		
		ByteBuffer compiledBuffer = BufferUtils.createByteBuffer(size);
		for (VoxelModelInstance instance : instances) {
			ByteBuffer instanceBuffer = instance.getCompiledVoxels();
			instance.setBufferLocation(compiledBuffer.position());
			compiledBuffer.put(instanceBuffer);
		}
		compiledBuffer.flip();
		
		instanceCount = compiledBuffer.capacity() / INSTANCE_ELEMENT_SIZE;
		
		if (voxelInstanceBuffer == -1) {
			initialiseVAO(compiledBuffer);
		} else {
			updateInstanceBuffer(compiledBuffer);
		}
		
		TimeProfiler.end("[P_GREEN_1]VoxelBatch.rebuildVoxels[]");
	}
	
	public void render(RenderPass pass, Camera camera, SceneContext scene) {
		TimeProfiler.begin("[P_GREEN_1]VoxelBatch.render - update[]");
		
		if (voxelVAO == -1 || voxelInstanceBuffer == -1) needsRebuild = true;
		
		if (needsRebuild) {
			rebuildVoxels(scene);
			needsRebuild = false;
		} else if (instancesNeedRemap) {
			Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, voxelInstanceBuffer);
			ByteBuffer mappedBuffer = GL15.glMapBuffer(Gdx.gl.GL_ARRAY_BUFFER, GL15.GL_WRITE_ONLY);
			
			for (VoxelModelInstance instance : instances) {
				if (!instance.isUpdated()) continue;
				
				mappedBuffer.position(instance.getBufferLocation());
				mappedBuffer.put(instance.compileVoxels());
				
				instance.setUpdated(false);
			}
			
			GL15.glUnmapBuffer(Gdx.gl.GL_ARRAY_BUFFER);
			Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, 0);
			
			instancesNeedRemap = false;
		}
		
		TimeProfiler.end("[P_GREEN_1]VoxelBatch.render - update[]");
		
		if (pass.isCheckCulling() && isCulled || instanceCount <= 0) return;
		
		ShaderProgram voxelShader = pass.getVoxelShader();
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		TimeProfiler.begin("[P_GREEN_1]VoxelBatch.render[]");
		
		voxelShader.begin();
		voxelShader.setUniformMatrix("u_projTrans", camera.combined);
		
		if (pass == RenderPass.SHADOW_STATIC_PASS || pass == RenderPass.SHADOW_DYNAMIC_PASS) {
			Light currentLight = scene.lightContext.getCurrentShadowMapLight();
			voxelShader.setUniformf("u_cameraFar", camera.far);
			voxelShader.setUniformf("u_lightPosition", currentLight.getPosition());
		}
		
		Gdx.gl.glEnable(Gdx.gl.GL_DEPTH_TEST);
		Gdx.gl.glEnable(Gdx.gl.GL_CULL_FACE);
		Gdx.gl.glCullFace(Gdx.gl.GL_BACK);
		
		GL30.glBindVertexArray(voxelVAO);
		
		GL31.glDrawArraysInstanced(
			Gdx.gl.GL_TRIANGLES,
			0,
			VoxelCube.CUBE_VERTICES.length / VoxelCube.CUBE_ELEMENT_COUNT,
			instanceCount
		);
		GL30.glBindVertexArray(0);
		
		Gdx.gl.glDisable(Gdx.gl.GL_CULL_FACE);
		Gdx.gl.glDisable(Gdx.gl.GL_DEPTH_TEST);
		
		voxelShader.end();
		
		TimeProfiler.end("[P_GREEN_1]VoxelBatch.render[]");
	}
	
	public void dispose() {
		Gdx.gl.glDeleteBuffer(voxelInstanceBuffer);
		GL30.glDeleteVertexArrays(voxelVAO);
	}
}
