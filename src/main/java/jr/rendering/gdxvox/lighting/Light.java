package jr.rendering.gdxvox.lighting;

import com.badlogic.gdx.math.Vector3;
import jr.rendering.gdxvox.context.LightContext;
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
	
	public Light(boolean enabled, Vector3 position, Vector3 positionOffset, Colour colour, float attenuationFactor) {
		this.enabled = enabled;
		this.position = position;
		this.positionOffset = positionOffset;
		this.colour = colour;
		this.attenuationFactor = attenuationFactor;
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
}
