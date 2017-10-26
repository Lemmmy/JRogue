package jr.rendering.gdxvox.context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.events.LevelChangeEvent;
import jr.rendering.gdxvox.lighting.Light;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL31;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class LightContext extends Context {
	public static final Color DEFAULT_AMBIENT_COLOUR = new Color(0x262c35ff);
	
	public static final int MAX_LIGHTS = 256;
	
	public static final int LIGHT_ELEMENT_COUNT = 4;
	public static final int LIGHT_ELEMENT_SIZE = 32;
	
	private Map<Entity, Light> lights = new HashMap<>();
	@Setter	private boolean lightsNeedUpdating = false;
	
	private int lightBufferHandle = -1;
	private int bufferSize = 0;
	
	@Setter private Color ambientLight = new Color(0x181818ff);
	
	public LightContext(Dungeon dungeon) {
		super(dungeon);
	}
	
	public void update(float delta) {
		if (!lightsNeedUpdating) return;
		rebuildLights();
		lightsNeedUpdating = false;
	}
	
	public void rebuildLights() {
		if (lightBufferHandle == -1) lightBufferHandle = Gdx.gl.glGenBuffer();
		
		Gdx.gl.glBindBuffer(GL31.GL_UNIFORM_BUFFER, lightBufferHandle);
		
		List<List<Float>> lightBuffers = lights.values().stream()
			.filter(Light::isEnabled)
			.limit(LightContext.MAX_LIGHTS)
			.map(Light::compileLight)
			.collect(Collectors.toList());
		
		bufferSize = 16 + lightBuffers.stream()
			.mapToInt(List::size)
			.sum() * 4;
		
		ByteBuffer compiledBuffer = BufferUtils.createByteBuffer(bufferSize);
		compiledBuffer.putInt((int) lights.values().stream()
			.filter(Light::isEnabled)
			.limit(LightContext.MAX_LIGHTS)
			.count()); // count
		compiledBuffer.putFloat(0.0f).putFloat(0.0f).putFloat(0.0f); // padding
		lightBuffers.forEach(buffer -> buffer.forEach(compiledBuffer::putFloat));
		compiledBuffer.flip();
		
		Gdx.gl.glBufferData(GL31.GL_UNIFORM_BUFFER, bufferSize, compiledBuffer, Gdx.gl.GL_DYNAMIC_DRAW);
	}
	
	@Override
	protected void onLevelChange(LevelChangeEvent levelChangeEvent) {
		super.onLevelChange(levelChangeEvent);
		lights.clear();
		lightsNeedUpdating = true;
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
}
