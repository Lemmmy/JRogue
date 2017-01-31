package jr.rendering.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import lombok.Getter;

@Getter
public enum ParticleEffectMap {
	WATER_STEP("water_step", 0, 250);
	
	private ParticleEffectPool pool;
	private ParticleEffect effect;
	
	ParticleEffectMap(String effectName) {
		this(effectName, 50, 250);
	}
	
	ParticleEffectMap(String effectName, int initialCapacity, int max) {
		effect = new ParticleEffect();
		effect.load(
			Gdx.files.internal("particles/" + effectName + ".particle"),
			Gdx.files.internal("textures")
		);
		
		pool = new ParticleEffectPool(effect, initialCapacity, max);
	}
}
