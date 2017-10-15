package jr.rendering.gdxvox.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import jr.ErrorHandler;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventListener;
import jr.dungeon.events.EventPriority;
import jr.dungeon.events.LevelChangeEvent;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SceneContext implements EventListener {
	public static final int MAX_LIGHTS = 128;
	
	public static final int LIGHT_ELEMENT_COUNT = 4;
	public static final int LIGHT_ELEMENT_SIZE = 32;
	
	public static final int G_BUFFERS_COUNT = 4;
	
	public static final int G_BUFFER_DIFFUSE = 0,
							G_BUFFER_NORMALS = 1,
							G_BUFFER_POSITION = 2,
							G_BUFFER_DEPTH = 3;
	
	public static final int[] G_BUFFERS_ATTACHMENTS = new int[] {
		GL30.GL_COLOR_ATTACHMENT0,
		GL30.GL_COLOR_ATTACHMENT1,
		GL30.GL_COLOR_ATTACHMENT2
	};
	
	private Dungeon dungeon;
	private Level level;
	
	@Getter private Map<Entity, Light> lights = new HashMap<>();
	@Getter @Setter	private boolean lightsNeedUpdating = false;
	
	@Getter private int lightBufferHandle = -1,
						gBuffersHandle = -1;
	@Getter private IntBuffer gBuffersTextures;
	
	@Getter @Setter private Camera screenCamera, worldCamera;
	
	public SceneContext(Dungeon dungeon) {
		this.dungeon = dungeon;
		this.level = dungeon.getLevel();
		this.dungeon.eventSystem.addListener(this);
		
		initialiseGBuffers(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	private void initialiseGBuffers(int width, int height) { // TODO: extract
		if (gBuffersTextures != null) {
			Gdx.gl.glDeleteTextures(G_BUFFERS_COUNT, gBuffersTextures);
			Gdx.gl.glDeleteFramebuffer(gBuffersHandle);
		}
		
		Gdx.gl.glBindFramebuffer(Gdx.gl.GL_FRAMEBUFFER, gBuffersHandle = Gdx.gl.glGenFramebuffer());
		
		gBuffersTextures = BufferUtils.createIntBuffer(G_BUFFERS_COUNT);
		Gdx.gl.glGenTextures(G_BUFFERS_COUNT, gBuffersTextures);
		
		for (int i = 0; i < G_BUFFERS_COUNT; i++) {
			int texHandle = gBuffersTextures.get(i);
			int texTarget = Gdx.gl.GL_TEXTURE_2D;
			int internalFormat = GL30.GL_RGB32F;
			int format = i == G_BUFFER_DEPTH ? Gdx.gl.GL_DEPTH_COMPONENT : Gdx.gl.GL_RGB;
			int type = Gdx.gl.GL_FLOAT;
			
			int attachment = GL30.GL_COLOR_ATTACHMENT0;
			
			switch (i) {
				case G_BUFFER_DIFFUSE:
					internalFormat = Gdx.gl.GL_RGB;
					type = Gdx.gl.GL_UNSIGNED_BYTE;
					break;
				case G_BUFFER_NORMALS:
					attachment = GL30.GL_COLOR_ATTACHMENT1;
					break;
				case G_BUFFER_POSITION:
					attachment = GL30.GL_COLOR_ATTACHMENT2;
					break;
				case G_BUFFER_DEPTH:
					attachment = GL30.GL_DEPTH_ATTACHMENT;
					internalFormat = Gdx.gl.GL_DEPTH_COMPONENT;
					type = Gdx.gl.GL_UNSIGNED_BYTE;
					break;
			}
			
			Gdx.gl.glBindTexture(texTarget, texHandle);
			
			Gdx.gl.glTexParameteri(texTarget, Gdx.gl.GL_TEXTURE_MIN_FILTER, Gdx.gl.GL_NEAREST);
			Gdx.gl.glTexParameteri(texTarget, Gdx.gl.GL_TEXTURE_MAG_FILTER, Gdx.gl.GL_NEAREST);
			
			Gdx.gl.glTexImage2D(
				texTarget,
				0,
				internalFormat,
				width,
				height,
				0,
				format,
				type,
				null
			);
			
			GL32.glFramebufferTexture(
				Gdx.gl.GL_FRAMEBUFFER,
				attachment,
				texHandle,
				0
			);
		}
		
		Gdx.gl.glBindFramebuffer(Gdx.gl.GL_FRAMEBUFFER, 0);
		
		if (Gdx.gl.glCheckFramebufferStatus(Gdx.gl.GL_FRAMEBUFFER) != Gdx.gl.GL_FRAMEBUFFER_COMPLETE) {
			ErrorHandler.error("fuck", new RuntimeException("shit"));
		}
	}
	
	public void update() {
		updateLights();
	}
	
	public void updateLights() { // TODO: extract
		if (!lightsNeedUpdating) return;
		rebuildLights();
		lightsNeedUpdating = false;
	}
	
	public void rebuildLights() {
		if (lightBufferHandle == -1) lightBufferHandle = Gdx.gl.glGenBuffer();
		
		Gdx.gl.glBindBuffer(GL31.GL_UNIFORM_BUFFER, lightBufferHandle);
		
		List<ByteBuffer> lightBuffers = lights.values().stream()
			.filter(Light::isEnabled)
			.limit(MAX_LIGHTS)
			.map(Light::compileLight)
			.collect(Collectors.toList());
		
		int size = 16 + lightBuffers.stream()
			.mapToInt(Buffer::capacity)
			.sum();
		
		ByteBuffer compiledBuffer = BufferUtils.createByteBuffer(size);
		compiledBuffer.putInt((int) lights.values().stream()
			.filter(Light::isEnabled)
			.limit(MAX_LIGHTS)
			.count()); // count
		compiledBuffer.putFloat(0.0f).putFloat(0.0f).putFloat(0.0f); // padding
		lightBuffers.forEach(compiledBuffer::put); // lights
		compiledBuffer.flip();
		
		Gdx.gl.glBufferData(GL31.GL_UNIFORM_BUFFER, size, compiledBuffer, Gdx.gl.GL_DYNAMIC_DRAW);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onLevelChange(LevelChangeEvent levelChangeEvent) {
		lights.clear();
		updateLights();
	}
	
	public void addLight(Entity emitter, Light light) {
		lights.put(emitter, light);
		lightsNeedUpdating = true;
	}
	
	public void removeLight(Entity emitter) {
		lights.remove(emitter);
		lightsNeedUpdating = true;
	}
	
	public void moveLight(Entity emitter) {
		lights.get(emitter).setPosition(new Vector3(emitter.getX(), 0, emitter.getY()));
		lightsNeedUpdating = true;
	}
	
	public void resize(int width, int height) {
		initialiseGBuffers(width, height);
	}
}
