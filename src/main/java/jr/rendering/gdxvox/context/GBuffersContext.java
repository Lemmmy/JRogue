package jr.rendering.gdxvox.context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import jr.dungeon.Dungeon;
import lombok.Getter;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

@Getter
public class GBuffersContext extends Context {
	private FrameBuffer frameBuffer;
	
	public GBuffersContext(Dungeon dungeon) {
		super(dungeon);
		
		initialiseGBuffers(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	private void initialiseGBuffers(int width, int height) {
		if (frameBuffer != null) frameBuffer.dispose();
		
		GLFrameBuffer.FrameBufferBuilder builder = new GLFrameBuffer.FrameBufferBuilder(width, height);
		
		builder.addColorTextureAttachment(GL11.GL_RGB8, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE); // diffuse
		builder.addColorTextureAttachment(GL30.GL_RGB32F, GL20.GL_RGB, GL20.GL_FLOAT); // normals
		builder.addColorTextureAttachment(GL30.GL_RGB32F, GL20.GL_RGB, GL20.GL_FLOAT); // position
		builder.addDepthTextureAttachment(GL20.GL_DEPTH_COMPONENT, GL20.GL_UNSIGNED_BYTE); // depth
		
		frameBuffer = builder.build();
	}
	
	public void resize(int width, int height) {
		initialiseGBuffers(width, height);
	}
}
