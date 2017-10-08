package jr.dungeon.entities.interfaces;

import jr.utils.Colour;

public interface LightEmitter {
	Colour getLightColour();
	float getLightAttenuationFactor();
	
	boolean isLightEnabled();
}
