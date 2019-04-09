package jr.rendering.entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import jr.dungeon.entities.Entity;
import jr.rendering.assets.Assets;
import jr.utils.Point;

import static jr.rendering.assets.Particles.particleFile;

public class EntityRendererParticleHound extends EntityRendererBasic {
    private String particleName;
    
    public EntityRendererParticleHound(String fileName, String particleName) {
        super(fileName);
        this.particleName = particleName;
    }
    
    @Override
    public void onLoad(Assets assets) {
        super.onLoad(assets);
        
        assets.particles.load(particleFile(particleName), p -> effectPool = new ParticleEffectPool(p, 100, 500));
    }
    
    @Override
    public int getParticleXOffset(Entity entity) {
        return 7;
    }
    
    @Override
    public int getParticleYOffset(Entity entity) {
        return 6;
    }
    
    @Override
    public boolean shouldDrawParticles(Entity entity, Point p) {
        return true;
    }
}
