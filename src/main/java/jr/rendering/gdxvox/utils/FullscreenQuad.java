package jr.rendering.gdxvox.utils;

import com.badlogic.gdx.Gdx;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

public class FullscreenQuad {
	public static final int QUAD_ELEMENT_COUNT = 4;
	public static final int QUAD_ELEMENT_SIZE = 16 * 4;
	
	public static final float[] QUAD_VERTICES = new float[] {
		0, 0, 0, 0,
		0, 640, 0, 1,
		800, 0, 1, 0,
		800, 640, 1, 1
	};
	
	private static int quadVBOHandle = -1;
	private static int quadVAOHandle = -1;
	
	private static void initialise() {
		GL30.glBindVertexArray(quadVAOHandle = GL30.glGenVertexArrays());
		
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, quadVBOHandle = Gdx.gl.glGenBuffer());
		
		FloatBuffer buf = BufferUtils.createFloatBuffer(QUAD_VERTICES.length);
		buf.put(QUAD_VERTICES);
		buf.flip();
		
		Gdx.gl.glBufferData(Gdx.gl.GL_ARRAY_BUFFER, QUAD_ELEMENT_SIZE, buf, Gdx.gl.GL_STATIC_DRAW);
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 2, Gdx.gl.GL_FLOAT, false, QUAD_ELEMENT_COUNT * 4, 0);
		
		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, 2, Gdx.gl.GL_FLOAT, false, QUAD_ELEMENT_COUNT * 4, 2 * 4);
		
		GL30.glBindVertexArray(0);
	}
	
	public static int getVAO() {
		if (quadVBOHandle == -1 || quadVAOHandle == -1) initialise();
		return quadVAOHandle;
	}
}
