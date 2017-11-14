package jr.rendering.gdxvox.context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.events.LevelChangeEvent;
import jr.rendering.gdxvox.lighting.Light;
import jr.rendering.utils.TimeProfiler;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL31;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class LightContext extends Context {
	public static int SHADOW_MAP_SIZE = -1;
	
	public static final float SHADOW_CAMERA_NEAR_Z = 0.25f;
	public static final float SHADOW_CAMERA_FAR_Z = 25f;
	
	public static final Color DEFAULT_AMBIENT_COLOUR = new Color(0x262c35ff);
	
	public static final int MAX_LIGHTS = 256;
	
	public static final int LIGHT_ELEMENT_COUNT = 4;
	public static final int LIGHT_ELEMENT_SIZE = 32;
	
	private Map<Entity, Light> lights = new HashMap<>();
	@Setter	private boolean lightsNeedUpdating = false;
	private Light currentShadowMapLight;
	
	private int lightBufferHandle = -1;
	private int bufferSize = 0;
	
	@Setter private Color ambientLight = new Color(0x181818ff);
	
	@Setter	private boolean shadowMapsNeedUpdating = false;
	private PerspectiveCamera shadowMapCamera;
	
	public LightContext(Dungeon dungeon) {
		super(dungeon);
		
		if (SHADOW_MAP_SIZE == -1) SHADOW_MAP_SIZE = JRogue.getSettings().getShadowMapSize();
		if (SHADOW_MAP_SIZE != -1) initialiseShadowMapCamera();
	}
	
	private void initialiseShadowMapCamera() {
		shadowMapCamera = new PerspectiveCamera(90f, LightContext.SHADOW_MAP_SIZE, LightContext.SHADOW_MAP_SIZE);
		shadowMapCamera.near = SHADOW_CAMERA_NEAR_Z;
		shadowMapCamera.far = SHADOW_CAMERA_FAR_Z;
	}
	
	public void update(float delta, SceneContext scene) {
		if (lightsNeedUpdating) {
			rebuildLights();
			lightsNeedUpdating = false;
		}
		
		if (shadowMapsNeedUpdating) {
			rebuildShadowMaps(scene);
			shadowMapsNeedUpdating = false;
		}
	}
	
	public void rebuildLights() {
		TimeProfiler.begin("[P_ORANGE_2]LightContext.rebuildLights[]");
		
		if (lightBufferHandle == -1) lightBufferHandle = Gdx.gl.glGenBuffer();
		
		Gdx.gl.glBindBuffer(GL31.GL_UNIFORM_BUFFER, lightBufferHandle);
		
		List<ByteBuffer> lightBuffers = new ArrayList<>();
		
		for (Light light : lights.values()) {
			if (!light.isEnabled()) continue;
			if (lightBuffers.size() >= MAX_LIGHTS) break;
			
			lightBuffers.add(light.compileLight());
		}
		
		bufferSize = 16 + lightBuffers.size() * LIGHT_ELEMENT_SIZE;
		
		ByteBuffer compiledBuffer = BufferUtils.createByteBuffer(bufferSize);
		compiledBuffer.putInt(lightBuffers.size()); // count
		compiledBuffer.putFloat(0.0f).putFloat(0.0f).putFloat(0.0f); // padding
		lightBuffers.forEach(compiledBuffer::put);
		compiledBuffer.flip();
		
		Gdx.gl.glBufferData(GL31.GL_UNIFORM_BUFFER, bufferSize, compiledBuffer, Gdx.gl.GL_DYNAMIC_DRAW);
		
		TimeProfiler.end("[P_ORANGE_2]LightContext.rebuildLights[]");
	}
	
	private void rebuildShadowMaps(SceneContext scene) {
		for (Light light : lights.values()) {
			currentShadowMapLight = light;
			light.renderShadowMaps(scene, this, shadowMapCamera);
		}
	}
	
	@Override
	protected void onLevelChange(LevelChangeEvent levelChangeEvent) {
		super.onLevelChange(levelChangeEvent);
		lights.values().forEach(Light::dispose);
		lights.clear();
		lightsNeedUpdating = true;
		shadowMapsNeedUpdating = true;
	}
	
	public void addLight(Entity emitter, Light light) {
		lights.put(emitter, light);
		lightsNeedUpdating = true;
		shadowMapsNeedUpdating = true;
	}
	
	public void removeLight(Entity emitter) {
		lights.remove(emitter);
		lightsNeedUpdating = true;
		shadowMapsNeedUpdating = true;
	}
	
	public void moveLight(Entity emitter) {
		lights.get(emitter).setPosition(new Vector3(emitter.getX(), 0, emitter.getY()));
		lightsNeedUpdating = true;
		shadowMapsNeedUpdating = true;
	}
	
	@Override
	public void dispose() {
		lights.values().forEach(Light::dispose);
	}
}
