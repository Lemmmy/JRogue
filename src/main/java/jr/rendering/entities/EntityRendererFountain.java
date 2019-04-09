package jr.rendering.entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import jr.dungeon.entities.Entity;
import jr.rendering.assets.Assets;
import jr.utils.Point;

import static jr.rendering.assets.Particles.particleFile;

public class EntityRendererFountain extends EntityRendererRandom {
	private static final int[] PARTICLE_Y_OFFSETS = new int[]{10, 13};
	
	public EntityRendererFountain(String fileName, int count) {
		super(fileName, count);
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.particles.load(particleFile("fountain"), p -> effectPool = new ParticleEffectPool(p, 100, 500));
	}
	
	@Override
	public int getParticleXOffset(Entity entity) {
		return 7;
	}
	
	@Override
	public int getParticleYOffset(Entity entity) {
		return PARTICLE_Y_OFFSETS[entity.getVisualID() % PARTICLE_Y_OFFSETS.length];
	}
	
	@Override
	public boolean shouldDrawParticles(Entity entity, Point p) {
		return true;
	}
	
	@Override
	public boolean shouldDrawParticlesOver(Entity entity, Point p) {
		return true;
	}
	
	@Override
	public float getParticleDeltaMultiplier(Entity entity, Point p) {
		return 0.75f;
	}
}
