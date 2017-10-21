package jr.rendering.gdxvox.primitives;

import com.badlogic.gdx.Gdx;
import jr.ErrorHandler;
import org.lwjgl.BufferUtils;

import java.nio.Buffer;

public class VoxelCube {
	public static final int CUBE_ELEMENT_COUNT = 6;
	public static final int CUBE_ELEMENT_SIZE = 6 * 4;
	
	public static final float[] CUBE_VERTICES = new float[] {
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
	
	private static void initialiseCubeBuffer() {
		Buffer cubeVerticesBuffer = BufferUtils.createFloatBuffer(CUBE_VERTICES.length)
			.put(CUBE_VERTICES).flip();
		
		cubeBuffer = Gdx.gl.glGenBuffer();
		
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, cubeBuffer);
		Gdx.gl.glBufferData(Gdx.gl.GL_ARRAY_BUFFER, CUBE_VERTICES.length * 4, cubeVerticesBuffer, Gdx.gl.GL_STATIC_DRAW);
	}
	
	public static int getVBO() {
		if (cubeBuffer == -1) initialiseCubeBuffer();
		return cubeBuffer;
	}
	
	public static void dispose() {
		if (cubeBuffer != -1) {
			Gdx.gl.glDeleteBuffer(cubeBuffer);
			cubeBuffer = -1;
		}
	}
}
