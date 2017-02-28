package jr.rendering.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import jr.rendering.gdx.entities.EntityMap;
import lombok.Getter;

@Getter
public enum ParticleEffectMap {
	WATER_STEP("water_step", 0, 250),
	ENTITY_FIRE("entity_fire", 0, 250, EntityMap.ENTITY_WIDTH / 4, EntityMap.ENTITY_HEIGHT - 2);
	
	private ParticleEffectPool pool;
	private ParticleEffect effect;
	
	private int xOffset, yOffset;
	private float deltaModifier;
	
	ParticleEffectMap(String effectName) {
		this(effectName, 50, 250, 0, 0, 0.25f);
	}
	
	ParticleEffectMap(String effectName, int initialCapacity, int max) {
		this(effectName, initialCapacity, max, EntityMap.ENTITY_WIDTH / 2, EntityMap.ENTITY_HEIGHT / 2, 0.25f);
	}
	
	ParticleEffectMap(String effectName, int initialCapacity, int max, int xOffset, int yOffset) {
		this(effectName, initialCapacity, max, xOffset, yOffset, 0.25f);
	}
	
	ParticleEffectMap(String effectName, int initialCapacity, int max, int xOffset, int yOffset, float deltaModifier) {
		effect = new ParticleEffect();
		effect.load(
			Gdx.files.internal("particles/" + effectName + ".particle"),
			Gdx.files.internal("textures")
		);
		
		pool = new ParticleEffectPool(effect, initialCapacity, max);
		
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		
		this.deltaModifier = deltaModifier;
	}
}
