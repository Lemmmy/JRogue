package jr.rendering.gdxvox.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SceneContext implements EventListener {
	public static final int MAX_LIGHTS = 64;
	
	public static final int LIGHT_ELEMENT_COUNT = 4;
	public static final int LIGHT_ELEMENT_SIZE = 3 * 4 + 3 * 4 + 4;
	
	private Dungeon dungeon;
	private Level level;
	
	@Getter private Map<Entity, Light> lights = new HashMap<>();
	@Getter @Setter	private boolean lightsNeedUpdating = false;
	
	@Getter private int lightBufferHandle = -1;
	
	public SceneContext(Dungeon dungeon) {
		this.dungeon = dungeon;
		this.level = dungeon.getLevel();
		this.dungeon.eventSystem.addListener(this);
	}
	
	public void update() {
		updateLights();
	}
	
	public void updateLights() {
		if (!lightsNeedUpdating) return;
		rebuildLights();
		lightsNeedUpdating = false;
	}
	
	public void rebuildLights() {
		if (lightBufferHandle == -1) lightBufferHandle = Gdx.gl.glGenBuffer();
		
		Gdx.gl.glBindBuffer(Gdx.gl.GL_ARRAY_BUFFER, lightBufferHandle);
		
		List<ByteBuffer> lightBuffers = lights.values().stream()
			.filter(Light::isEnabled)
			.limit(MAX_LIGHTS)
			.map(Light::compileLight)
			.collect(Collectors.toList());
		
		int size = 4 + lightBuffers.stream()
			.mapToInt(Buffer::capacity)
			.sum();
		
		ByteBuffer compiledBuffer = BufferUtils.createByteBuffer(size);
		compiledBuffer.putInt((int) lights.values().stream()
			.filter(Light::isEnabled)
			.limit(MAX_LIGHTS)
			.count());
		lightBuffers.forEach(compiledBuffer::put);
		compiledBuffer.flip();
		
		Gdx.gl.glBufferData(Gdx.gl.GL_ARRAY_BUFFER, size, compiledBuffer, Gdx.gl.GL_DYNAMIC_DRAW);
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
}
