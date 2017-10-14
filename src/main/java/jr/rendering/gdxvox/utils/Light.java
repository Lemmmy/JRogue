package jr.rendering.gdxvox.utils;

import com.badlogic.gdx.math.Vector3;
import jr.utils.Colour;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

@Getter
@Setter
public class Light {
	private boolean enabled;
	
	private Vector3 position;
	private Vector3 positionOffset;
	private Colour colour;
	private float attenuationFactor;
	
	public Light(boolean enabled, Vector3 position, Vector3 positionOffset, Colour colour, float attenuationFactor) {
		this.enabled = enabled;
		this.position = position;
		this.positionOffset = positionOffset;
		this.colour = colour;
		this.attenuationFactor = attenuationFactor;
	}
	
	public ByteBuffer compileLight() {
		int size = SceneContext.LIGHT_ELEMENT_SIZE;
		
		ByteBuffer buf = BufferUtils.createByteBuffer(size);
		
		// position
		buf.putFloat(position.x + positionOffset.x)
			.putFloat(position.y + positionOffset.y)
			.putFloat(position.z + positionOffset.z);
		
		buf.putFloat(0f); // padding
		
		// colour
		buf.putFloat(colour.r).putFloat(colour.g).putFloat(colour.b);
		
		// attenuation factor
		buf.putFloat(attenuationFactor);
		
		buf.flip();
		
		return buf;
	}
}
