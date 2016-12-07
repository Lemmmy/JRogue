package pw.lemmmy.jrogue.rendering.gdx.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

public class EntityRendererFountain extends EntityRendererRandom {
	private static final int[] PARTICLE_Y_OFFSETS = new int[]{6, 3};

	public EntityRendererFountain(int sheetX, int sheetY, int count) {
		super(sheetX, sheetY, count);

		ParticleEffect effect = new ParticleEffect();
		effect.load(Gdx.files.internal("fountain.particle"), Gdx.files.internal(""));

		effectPool = new ParticleEffectPool(effect, 100, 500);
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
