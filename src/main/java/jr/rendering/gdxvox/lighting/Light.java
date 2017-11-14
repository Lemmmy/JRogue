package jr.rendering.gdxvox.lighting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBufferCubemap;
import com.badlogic.gdx.math.Vector3;
import jr.rendering.gdxvox.components.RenderPass;
import jr.rendering.gdxvox.context.LightContext;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.utils.FrameBufferUtils;
import jr.rendering.utils.ScreenshotFactory;
import jr.rendering.utils.TimeProfiler;
import jr.utils.Colour;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

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
	
	public ByteBuffer compileLight() {
		ByteBuffer buf = BufferUtils.createByteBuffer(LightContext.LIGHT_ELEMENT_SIZE);
		
		// position
		buf.putFloat(position.x + positionOffset.x);
		buf.putFloat(position.y + positionOffset.y);
		buf.putFloat(position.z + positionOffset.z);
		
		buf.putFloat(0f); // padding
		
		// colour
		buf.putFloat(colour.r);
		buf.putFloat(colour.g);
		buf.putFloat(colour.b);
		
		// attenuation factor
		buf.putFloat(attenuationFactor);
		
		buf.flip();
		return buf;
	}
	
	public void renderShadowMaps(SceneContext sceneContext, LightContext lightContext, Camera camera) {
		TimeProfiler.begin("[P_GREY_3]Light.renderShadowMaps[]");
		
		fbo.begin();
		fbo.bind();
		
		for (Cubemap.CubemapSide side : Cubemap.CubemapSide.values()) {
			fbo.nextSide();
			
			if (side == Cubemap.CubemapSide.PositiveY) continue; // top side not needed
			
			FrameBufferUtils.rotateToSide(side, camera);
			camera.position.set(position).add(positionOffset);
			camera.update();
			
			
			Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT | Gdx.gl.GL_DEPTH_BUFFER_BIT);
			
			sceneContext.renderAllMaps(RenderPass.SHADOW_STATIC_PASS, camera);
			
			/* ScreenshotFactory.saveScreenshot(String.format(
				"light-shadowmap-cube-%s-%f-%f-%f",
				side.name(),
				position.x, position.y, position.z
			)); */
		}
		
		fbo.end();
		
		TimeProfiler.end("[P_GREY_3]Light.renderShadowMaps[]");
	}
	
	public void dispose() {
		if (fbo != null) fbo.dispose();
	}
}
