package jr.rendering.tiles;

import lombok.Data;

@Data(staticConstructor = "create")
public class ReflectionSettings {
	private final float waveAmplitude;
	private final float waveFrequency;
	private final float waveTimeScale;
	
	private final float fadeAmplitude;
	private final float fadeBase;
}