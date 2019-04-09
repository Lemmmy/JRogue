package jr.rendering.entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.rendering.assets.Assets;
import jr.utils.Point;

import static jr.rendering.assets.Particles.particleFile;

public class EntityRendererCandlestick extends EntityRendererBasic {
    public EntityRendererCandlestick(String fileName) {
        super(fileName);
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        
        assets.particles.load(particleFile("candlestick_fire"), p -> effectPool = new ParticleEffectPool(p, 50, 500));
    }
    
    @Override
    public int getParticleXOffset(Entity entity) {
        return 7;
    }
    
    @Override
    public int getParticleYOffset(Entity entity) {
        return 15;
    }
    
    @Override
    public boolean shouldDrawParticles(Entity entity, Point p) {
        return entity.getAppearance() == EntityAppearance.APPEARANCE_CANDLESTICK;
    }
    
    @Override
    public boolean shouldDrawParticlesOver(Entity entity, Point p) {
        return false;
    }
    
    @Override
    public float getParticleDeltaMultiplier(Entity entity, Point p) {
        return 0.75f;
    }
}
