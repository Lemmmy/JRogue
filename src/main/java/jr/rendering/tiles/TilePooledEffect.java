package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TilePooledEffect {
	private int x;
	private int y;
	
	private ParticleEffectPool.PooledEffect pooledEffect;
}
