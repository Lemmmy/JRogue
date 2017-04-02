package jr.rendering.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;

public class EntityRendererParticleHound extends EntityRendererBasic {
	protected TextureRegion image;
	
	public EntityRendererParticleHound(int sheetX, int sheetY, String particleName) {
		super("textures/entities.png", sheetX, sheetY);
		
		ParticleEffect effect = new ParticleEffect();
		effect.load(Gdx.files.internal("particles/" + particleName + ".particle"), Gdx.files.internal("textures"));
		
		effectPool = new ParticleEffectPool(effect, 100, 500);
	}
	
	@Override
	public int getParticleXOffset(Entity entity) {
		return 7;
	}
	
	@Override
	public int getParticleYOffset(Entity entity) {
		return 10;
	}
	
	@Override
	public boolean shouldDrawParticles(Dungeon dungeon, Entity entity, int x, int y) {
		return true;
	}
}
