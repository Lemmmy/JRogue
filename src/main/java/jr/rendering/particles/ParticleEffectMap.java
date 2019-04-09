package jr.rendering.particles;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import jr.rendering.assets.Assets;
import jr.rendering.assets.RegisterAssetManager;
import jr.rendering.assets.UsesAssets;
import jr.rendering.entities.EntityMap;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;

import static jr.rendering.assets.Particles.particleFile;

@Getter
@RegisterAssetManager
public enum ParticleEffectMap implements UsesAssets {
    WATER_STEP("water_step", 0, 250),
    ENTITY_FIRE("entity_fire", 0, 250, EntityMap.ENTITY_WIDTH / 4, 2);
    
    private String fileName;
    
    private ParticleEffectPool pool;
    private ParticleEffect effect;
    
    private int initialCapacity, max, xOffset, yOffset;
    private float deltaModifier;
    
    ParticleEffectMap(String fileName) {
        this(fileName, 50, 250);
    }
    
    ParticleEffectMap(String fileName, int initialCapacity, int max) {
        this(fileName, initialCapacity, max, EntityMap.ENTITY_WIDTH / 2, EntityMap.ENTITY_HEIGHT / 2, 0.25f);
    }
    
    ParticleEffectMap(String fileName, int initialCapacity, int max, int xOffset, int yOffset) {
        this(fileName, initialCapacity, max, xOffset, yOffset, 0.25f);
    }
    
    ParticleEffectMap(String fileName, int initialCapacity, int max, int xOffset, int yOffset, float deltaModifier) {
        this.fileName = fileName;
        
        this.initialCapacity = initialCapacity;
        this.max = max;
        
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        
        this.deltaModifier = deltaModifier;
    }
    
    @Override
    public void onLoad(Assets assets) {
        assets.particles.load(particleFile(fileName), p -> pool = new ParticleEffectPool(p, initialCapacity, max));
    }
    
    public static Collection<? extends UsesAssets> getAssets() {
        return Arrays.asList(values());
    }
}
