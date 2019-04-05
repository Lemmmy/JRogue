package jr.rendering.entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.rendering.assets.Assets;

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
	public boolean shouldDrawParticles(Dungeon dungeon, Entity entity, int x, int y) {
		return true;
	}
	
	@Override
	public boolean shouldDrawParticlesOver(Dungeon dungeon, Entity entity, int x, int y) {
		return true;
	}
	
	@Override
	public float getParticleDeltaMultiplier(Dungeon dungeon, Entity entity, int x, int y) {
		return 0.75f;
	}
}
