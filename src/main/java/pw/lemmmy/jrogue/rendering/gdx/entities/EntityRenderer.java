package pw.lemmmy.jrogue.rendering.gdx.entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;

public abstract class EntityRenderer {
	protected ParticleEffectPool effectPool;
	
	public abstract void draw(SpriteBatch batch, Dungeon dungeon, Entity entity);
	
	protected TextureRegion getImageFromSheet(String sheetName, int sheetX, int sheetY) {
		return ImageLoader.getImageFromSheet(sheetName, sheetX, sheetY);
	}
	
	protected void drawTile(SpriteBatch batch, TextureRegion image, int x, int y) {
		if (image != null) {
			int width = EntityMap.ENTITY_WIDTH;
			int height = EntityMap.ENTITY_HEIGHT;
			
			batch.draw(image, x * width + 0.01f, y * height + 0.01f);
		}
	}
	
	public ParticleEffectPool getParticleEffectPool(Entity entity) {
		return effectPool;
	}
	
	public int getParticleXOffset(Entity entity) {
		return EntityMap.ENTITY_WIDTH / 2;
	}
	
	public int getParticleYOffset(Entity entity) {
		return EntityMap.ENTITY_HEIGHT / 2;
	}
	
	public boolean shouldDrawParticles(Dungeon dungeon, Entity entity, int x, int y) {
		return true;
	}
	
	public boolean shouldDrawParticlesOver(Dungeon dungeon, Entity entity, int x, int y) {
		return false;
	}
	
	public float getParticleDeltaMultiplier(Dungeon dungeon, Entity entity, int x, int y) {
		return 0.25f;
	}
}
