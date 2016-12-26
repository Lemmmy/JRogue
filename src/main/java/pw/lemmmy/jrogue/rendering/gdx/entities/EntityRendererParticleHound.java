package pw.lemmmy.jrogue.rendering.gdx.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

public class EntityRendererParticleHound extends EntityRenderer {
	protected TextureRegion image;
	
	public EntityRendererParticleHound(int sheetX, int sheetY, String particleName) {
		image = getImageFromSheet("entities.png", sheetX, sheetY);
		
		ParticleEffect effect = new ParticleEffect();
		effect.load(Gdx.files.internal(particleName + ".particle"), Gdx.files.internal(""));
		
		effectPool = new ParticleEffectPool(effect, 100, 500);
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity) {
		drawTile(batch, image, entity.getLastSeenX(), entity.getLastSeenY());
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
