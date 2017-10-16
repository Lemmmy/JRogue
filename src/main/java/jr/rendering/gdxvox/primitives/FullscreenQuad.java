package jr.rendering.gdxvox.primitives;

import com.badlogic.gdx.Gdx;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class FullscreenQuad {
	public static final int QUAD_ELEMENT_COUNT = 4;
	public static final int QUAD_ELEMENT_SIZE = 16 * 4;
	
	public static final float[] QUAD_VERTICES_POSITION = new float[] {
		0, 0,
		0, 1,
		1, 0,
		1, 1
	};
	
	public static final float[] QUAD_VERTICES_UV = new float[] {
		0, 0,
		0, 1,
		1, 0,
		1, 1
	};
	
	private static final Map<Dimension, Integer> quadsVAO = new HashMap<>();
	private static final Map<Dimension, Integer> quadsVBO = new HashMap<>();
	
	private static int initialise(Dimension dimension) {
		int width = (int) dimension.getWidth(), height = (int) dimension.getHeight();
		int size = QUAD_VERTICES_POSITION.length + QUAD_VERTICES_UV.length;
		float[] compiledVertices = new float[size];
		int quadVAOHandle, quadVBOHandle;
		
		for (int i = 0; i < QUAD_ELEMENT_COUNT; i++) {
			compiledVertices[(i * 4)] = QUAD_VERTICES_POSITION[(i * 2)] * width;
			compiledVertices[(i * 4) + 1] = QUAD_VERTICES_POSITION[(i * 2) + 1] * height;
			compiledVertices[(i * 4) + 2] = QUAD_VERTICES_UV[(i * 2)];
			compiledVertices[(i * 4) + 3] = QUAD_VERTICES_UV[(i * 2) + 1];
		}
		
		GL30.glBindVertexArray(quadVAOHandle = GL30.glGenVertexArrays());
		
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, quadVBOHandle = Gdx.gl.glGenBuffer());
		
		FloatBuffer buf = BufferUtils.createFloatBuffer(compiledVertices.length);
		buf.put(compiledVertices);
		buf.flip();
		
		Gdx.gl.glBufferData(Gdx.gl.GL_ARRAY_BUFFER, QUAD_ELEMENT_SIZE, buf, Gdx.gl.GL_STATIC_DRAW);
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 2, Gdx.gl.GL_FLOAT, false, QUAD_ELEMENT_COUNT * 4, 0);
		
		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, 2, Gdx.gl.GL_FLOAT, false, QUAD_ELEMENT_COUNT * 4, 2 * 4);
		
		GL30.glBindVertexArray(0);
		
		quadsVBO.put(dimension, quadVBOHandle);
		return quadsVAO.put(dimension, quadVAOHandle);
	}
	
	public static int getVAO(Dimension d) {
		if (quadsVAO.containsKey(d)) return quadsVAO.get(d);
		return initialise(d);
	}
	
	public static void deleteVAO(Dimension d) {
		if (quadsVAO.containsKey(d)) {
			GL30.glDeleteVertexArrays(quadsVAO.get(d));
			quadsVAO.remove(d);
		}
		
		if (quadsVBO.containsKey(d)) {
			Gdx.gl.glDeleteBuffer(quadsVBO.get(d));
			quadsVBO.remove(d);
		}
	}
}
