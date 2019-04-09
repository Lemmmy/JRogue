package jr.rendering.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader.ParticleEffectParameter;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

public class Particles extends AssetHandler<ParticleEffect, ParticleEffectParameter> {
    public Particles(Assets assets) {
        super(assets);
    }
    
    public static String particleFile(String fileName) {
        return fileName + ".particle";
    }
    
    @Override
    protected Class<ParticleEffect> getAssetClass() {
        return ParticleEffect.class;
    }
    
    @Override
    public String getFileNamePrefix() {
        return "particles/";
    }
    
    @Override
    public ParticleEffectParameter getAssetParameters(String fileName) {
        ParticleEffectParameter parameters = new ParticleEffectParameter();
        parameters.imagesDir = Gdx.files.internal("textures/particles"); // TODO: configurable?
        return parameters;
    }
}
