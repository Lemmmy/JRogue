package jr.rendering.entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.rendering.assets.Assets;

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
		return 1;
	}
	
	@Override
	public boolean shouldDrawParticles(Dungeon dungeon, Entity entity, int x, int y) {
		return entity.getAppearance() == EntityAppearance.APPEARANCE_CANDLESTICK;
	}
	
	@Override
	public boolean shouldDrawParticlesOver(Dungeon dungeon, Entity entity, int x, int y) {
		return false;
	}
	
	@Override
	public float getParticleDeltaMultiplier(Dungeon dungeon, Entity entity, int x, int y) {
		return 0.75f;
	}
}
