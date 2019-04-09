package jr.rendering.entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import jr.dungeon.entities.Entity;
import jr.utils.Point;

public class EntityPooledEffect {
    private Entity entity;
    private EntityRenderer renderer;
    
    public final Point position;
    
    private boolean over;
    
    private ParticleEffectPool.PooledEffect pooledEffect;
    
    public EntityPooledEffect(Entity entity,
                              EntityRenderer renderer,
                              Point position,
                              boolean over,
                              ParticleEffectPool.PooledEffect pooledEffect) {
        this.entity = entity;
        this.renderer = renderer;
        this.position = position;
        this.over = over;
        
        this.pooledEffect = pooledEffect;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public EntityRenderer getRenderer() {
        return renderer;
    }
    
    public boolean shouldDrawOver() {
        return over;
    }
    
    public ParticleEffectPool.PooledEffect getPooledEffect() {
        return pooledEffect;
    }
}
