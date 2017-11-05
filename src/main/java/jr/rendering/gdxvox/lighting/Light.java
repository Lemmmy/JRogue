package jr.rendering.gdxvox.lighting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBufferCubemap;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.math.Vector3;
import jr.JRogue;
import jr.rendering.gdxvox.components.RenderPass;
import jr.rendering.gdxvox.context.LightContext;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.utils.FrameBufferUtils;
import jr.rendering.utils.ScreenshotFactory;
import jr.utils.Colour;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Light {
	private boolean enabled;
	
	private Vector3 position;
	private Vector3 positionOffset;
	private Colour colour;
	private float attenuationFactor;
	
	private FrameBufferCubemap fbo;
	
	public Light(boolean enabled, Vector3 position, Vector3 positionOffset, Colour colour, float attenuationFactor) {
		this.enabled = enabled;
		this.position = position;
		this.positionOffset = positionOffset;
		this.colour = colour;
		this.attenuationFactor = attenuationFactor;
		
		if (LightContext.SHADOW_MAP_SIZE != -1) initialiseFBO();
	}
	
	private void initialiseFBO() {
		this.fbo = FrameBufferCubemap.createFrameBufferCubemap(
			Pixmap.Format.RGBA8888,
			LightContext.SHADOW_MAP_SIZE, LightContext.SHADOW_MAP_SIZE,
			true
		);
	}
	
	public List<Float> compileLight() {
		int size = LightContext.LIGHT_ELEMENT_SIZE;
		
		List<Float> buf = new ArrayList<>();
		
		// position
		buf.add(position.x + positionOffset.x);
		buf.add(position.y + positionOffset.y);
		buf.add(position.z + positionOffset.z);
		
		buf.add(0f); // padding
		
		// colour
		buf.add(colour.r);
		buf.add(colour.g);
		buf.add(colour.b);
		
		// attenuation factor
		buf.add(attenuationFactor);
		
		return buf;
	}
	
	public void renderShadowMaps(SceneContext sceneContext, LightContext lightContext, Camera camera) {
		fbo.begin();
		fbo.bind();
		
		for (Cubemap.CubemapSide side : Cubemap.CubemapSide.values()) {
			fbo.nextSide();
			FrameBufferUtils.rotateToSide(side, camera);
			camera.position.set(position);
			camera.update();
			
			Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT | Gdx.gl.GL_DEPTH_BUFFER_BIT);
			
			sceneContext.renderAllMaps(RenderPass.SHADOW_STATIC_PASS, camera);
			
			ScreenshotFactory.saveScreenshot(String.format(
				"light-shadowmap-cube-%s-%f-%f-%f",
				side.name(),
				position.x, position.y, position.z
			));
		}
		
		fbo.end();
	}
	
	public void dispose() {
		if (fbo != null) fbo.dispose();
	}
}
