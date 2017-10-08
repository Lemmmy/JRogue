package jr.rendering.gdxvox.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import jr.rendering.gdx2d.utils.ShaderLoader;
import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public abstract class VoxelBatch<ObjectV> {
	public static final int INSTANCE_ELEMENT_COUNT = 6;
	public static final int INSTANCE_ELEMENT_SIZE = 6 * 4;
	
	public static final int CUBE_ELEMENT_COUNT = 3;
	public static final int CUBE_ELEMENT_SIZE = 3 * 4;
	
	private static final float[] CUBE_VERTICES = new float[] {
		0.03125f, -0.03125f, -0.03125f, 
		-0.03125f, -0.03125f, 0.03125f, 
		-0.03125f, -0.03125f, -0.03125f, 
		0.03125f, -0.03125f, -0.03125f, 
		0.03125f, -0.03125f, 0.03125f, 
		-0.03125f, -0.03125f, 0.03125f, 
		-0.03125f, 0.03125f, -0.03125f, 
		-0.03125f, -0.03125f, -0.03125f, 
		-0.03125f, -0.03125f, 0.03125f, 
		-0.03125f, 0.03125f, 0.03125f, 
		-0.03125f, 0.03125f, -0.03125f, 
		-0.03125f, -0.03125f, 0.03125f, 
		-0.03125f, 0.03125f, 0.03125f, 
		-0.03125f, -0.03125f, 0.03125f, 
		0.03125f, 0.03125f, 0.03125f, 
		0.03125f, 0.03125f, 0.03125f, 
		-0.03125f, -0.03125f, 0.03125f, 
		0.03125f, -0.03125f, 0.03125f, 
		0.03125f, 0.03125f, 0.03125f, 
		0.03125f, -0.03125f, 0.03125f, 
		0.03125f, -0.03125f, -0.03125f, 
		0.03125f, 0.03125f, -0.03125f, 
		0.03125f, -0.03125f, -0.03125f, 
		-0.03125f, 0.03125f, -0.03125f, 
		-0.03125f, 0.03125f, -0.03125f, 
		0.03125f, -0.03125f, -0.03125f, 
		-0.03125f, -0.03125f, -0.03125f, 
		-0.03125f, 0.03125f, -0.03125f, 
		-0.03125f, 0.03125f, 0.03125f, 
		0.03125f, 0.03125f, -0.03125f, 
		-0.03125f, 0.03125f, 0.03125f, 
		0.03125f, 0.03125f, 0.03125f, 
		0.03125f, 0.03125f, -0.03125f, 
		0.03125f, -0.03125f, -0.03125f, 
		0.03125f, 0.03125f, -0.03125f, 
		0.03125f, 0.03125f, 0.03125f
	};
	
	private static int cubeBuffer = -1;
	
	private int voxelVAO, voxelInstanceBuffer = -1;
	private int instanceCount = 0;
	private ShaderProgram voxelShader;
	
	private Map<ObjectV, BatchedVoxelModel> objects = new HashMap<>();
	
	private boolean needsUpdate;
	
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
	
	private void initialiseVAO(FloatBuffer voxelsBuffer) {
		voxelInstanceBuffer = Gdx.gl.glGenBuffer();
		
		voxelVAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(voxelVAO);
		
		// voxel cube buffer
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, cubeBuffer);
		// position
		Gdx.gl.glEnableVertexAttribArray(0);
		Gdx.gl.glVertexAttribPointer(0, 3, Gdx.gl.GL_FLOAT, false, CUBE_ELEMENT_SIZE, 0);
		
		// instance buffer
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, voxelInstanceBuffer);
		Gdx.gl.glBufferData(Gdx.gl.GL_ARRAY_BUFFER, voxelsBuffer.capacity() * 4, voxelsBuffer, Gdx.gl.GL_STATIC_DRAW);
		// instance position
		Gdx.gl.glEnableVertexAttribArray(1);
		Gdx.gl.glVertexAttribPointer(1, 3, Gdx.gl.GL_FLOAT, false, INSTANCE_ELEMENT_SIZE, 0);
		GL33.glVertexAttribDivisor(1, 1);
		// instance colour
		Gdx.gl.glEnableVertexAttribArray(2);
		Gdx.gl.glVertexAttribPointer(2, 3, Gdx.gl.GL_FLOAT, false, INSTANCE_ELEMENT_SIZE, 3 * 4);
		GL33.glVertexAttribDivisor(2, 1);
		
		GL30.glBindVertexArray(0);
		
		voxelShader = ShaderLoader.getProgram("shaders/voxel");
	}
	
	public boolean contains(ObjectV object) {
		return objects.containsKey(object);
	}
	
	public void add(ObjectV object, BatchedVoxelModel model) {
		objects.put(object, model);
		needsUpdate = true;
	}
	
	public void remove(ObjectV object) {
		objects.remove(object);
		needsUpdate = true;
	}
	
	public void clear() {
		objects.clear();
		needsUpdate = true;
	}
	
	public void update() {
		if (!needsUpdate) return;
		rebuildVoxels();
		needsUpdate = false;
	}
	
	public void rebuildVoxels() {
		List<FloatBuffer> voxelBuffers = objects.values().stream()
			.map(BatchedVoxelModel::compileVoxels)
			.collect(Collectors.toList());
		
		int size = voxelBuffers.stream()
			.mapToInt(Buffer::capacity)
			.sum();
		
		FloatBuffer compiledBuffer = BufferUtils.createFloatBuffer(size);
		voxelBuffers.forEach(compiledBuffer::put);
		compiledBuffer.flip();
		
		instanceCount = objects.values().stream()
			.mapToInt(model -> model.getVoxels().length)
			.sum();
		
		if (voxelInstanceBuffer == -1) initialiseVAO(compiledBuffer);
	}
	
	public void render(Camera camera) {
		if (voxelVAO == -1 || voxelInstanceBuffer == -1 || voxelShader == null) {
			needsUpdate = true;
			update();
		}
		
		voxelShader.begin();
		voxelShader.setUniformMatrix("u_projTrans", camera.combined);
		
		Gdx.gl.glEnable(Gdx.gl.GL_DEPTH_TEST);
		Gdx.gl.glEnable(Gdx.gl.GL_CULL_FACE);
		Gdx.gl.glCullFace(Gdx.gl.GL_BACK);
		
		GL30.glBindVertexArray(voxelVAO);
		GL31.glDrawArraysInstanced(Gdx.gl.GL_TRIANGLES, 0, CUBE_VERTICES.length / CUBE_ELEMENT_COUNT, instanceCount);
		GL30.glBindVertexArray(0);
		
		Gdx.gl.glDisable(Gdx.gl.GL_CULL_FACE);
		Gdx.gl.glDisable(Gdx.gl.GL_DEPTH_TEST);
		
		voxelShader.end();
	}
}
