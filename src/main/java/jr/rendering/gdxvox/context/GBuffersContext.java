package jr.rendering.gdxvox.context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import jr.ErrorHandler;
import jr.dungeon.Dungeon;
import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import java.nio.IntBuffer;

public class GBuffersContext extends Context {
	public static final int[] G_BUFFERS_ATTACHMENTS = new int[] {
		GL30.GL_COLOR_ATTACHMENT0,
		GL30.GL_COLOR_ATTACHMENT1,
		GL30.GL_COLOR_ATTACHMENT2
	};
	
	@Getter private int gBuffersHandle = -1;
	@Getter private IntBuffer gBuffersTextures;
	
	public GBuffersContext(Dungeon dungeon) {
		super(dungeon);
		
		initialiseGBuffers(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	private void initialiseGBuffers(int width, int height) { // TODO: extract
		int bufferCount = GBuffers.values().length;
		
		if (gBuffersTextures != null) {
			Gdx.gl.glDeleteTextures(bufferCount, gBuffersTextures);
			Gdx.gl.glDeleteFramebuffer(gBuffersHandle);
		}
		
		Gdx.gl.glBindFramebuffer(Gdx.gl.GL_FRAMEBUFFER, gBuffersHandle = Gdx.gl.glGenFramebuffer());
		
		gBuffersTextures = BufferUtils.createIntBuffer(bufferCount);
		Gdx.gl.glGenTextures(bufferCount, gBuffersTextures);
		
		for (GBuffers buffer : GBuffers.values()) {
			int i = buffer.ordinal();
			int texHandle = gBuffersTextures.get(i);
			int texTarget = Gdx.gl.GL_TEXTURE_2D;
			
			Gdx.gl.glBindTexture(texTarget, texHandle);
			
			Gdx.gl.glTexParameteri(texTarget, Gdx.gl.GL_TEXTURE_MIN_FILTER, Gdx.gl.GL_NEAREST);
			Gdx.gl.glTexParameteri(texTarget, Gdx.gl.GL_TEXTURE_MAG_FILTER, Gdx.gl.GL_NEAREST);
			
			Gdx.gl.glTexImage2D(
				texTarget,
				0,
				buffer.getInternalFormat(),
				width,
				height,
				0,
				buffer.getFormat(),
				buffer.getType(),
				null
			);
			
			GL32.glFramebufferTexture(
				Gdx.gl.GL_FRAMEBUFFER,
				buffer.getAttachment(),
				texHandle,
				0
			);
		}
		
		Gdx.gl.glBindFramebuffer(Gdx.gl.GL_FRAMEBUFFER, 0);
		
		if (Gdx.gl.glCheckFramebufferStatus(Gdx.gl.GL_FRAMEBUFFER) != Gdx.gl.GL_FRAMEBUFFER_COMPLETE) {
			ErrorHandler.error("Error initialising GBuffers", new RuntimeException("Error initialising GBuffers"));
		}
	}
	
	public int getHandle(GBuffers buffer) {
		return gBuffersTextures.get(buffer.ordinal());
	}
	
	public void bindTextures() {
		for (GBuffers buffer : GBuffers.values()) {
			Gdx.gl.glActiveTexture(buffer.getTextureUnit());
			Gdx.gl.glBindTexture(Gdx.gl.GL_TEXTURE_2D, getHandle(buffer));
		}
	}
	
	@Getter
	public enum GBuffers {
		DIFFUSE(
			GL30.GL_COLOR_ATTACHMENT0,
			GL20.GL_RGB,
			GL20.GL_RGB,
			GL20.GL_UNSIGNED_BYTE,
			GL20.GL_TEXTURE0
		),
		NORMALS(
			GL30.GL_COLOR_ATTACHMENT1,
			GL30.GL_RGB32F,
			GL20.GL_RGB,
			GL20.GL_FLOAT,
			GL20.GL_TEXTURE1
		),
		POSITION(
			GL30.GL_COLOR_ATTACHMENT2,
			GL30.GL_RGB32F,
			GL20.GL_RGB,
			GL20.GL_FLOAT,
			GL20.GL_TEXTURE2
		),
		DEPTH(
			GL30.GL_DEPTH_ATTACHMENT,
			GL20.GL_DEPTH_COMPONENT,
			GL20.GL_DEPTH_COMPONENT,
			GL20.GL_UNSIGNED_BYTE,
			GL20.GL_TEXTURE3
		);
		
		private int attachment, internalFormat, format, type, textureUnit;
		
		GBuffers(int attachment, int internalFormat, int format, int type, int textureUnit) {
			this.attachment = attachment;
			this.internalFormat = internalFormat;
			this.format = format;
			this.type = type;
			this.textureUnit = textureUnit;
		}
	}
	
	public void resize(int width, int height) {
		initialiseGBuffers(width, height);
	}
}
