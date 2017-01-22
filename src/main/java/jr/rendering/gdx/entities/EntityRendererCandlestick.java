package jr.rendering.gdx.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;

public class EntityRendererCandlestick extends EntityRendererBasic {
	public EntityRendererCandlestick(int sheetX, int sheetY) {
		super("entities.png", sheetX, sheetY);
		
		ParticleEffect torchEffect = new ParticleEffect();
		torchEffect.load(Gdx.files.internal("candlestick_fire.particle"), Gdx.files.internal(""));
		
		effectPool = new ParticleEffectPool(torchEffect, 50, 500);
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
