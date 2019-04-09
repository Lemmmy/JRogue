package jr.rendering.tiles;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import jr.utils.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TilePooledEffect {
    private Point position;
    private ParticleEffectPool.PooledEffect pooledEffect;
}
