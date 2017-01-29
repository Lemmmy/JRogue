package jr.rendering.gdx.entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.rendering.gdx.utils.ImageLoader;
import lombok.Getter;
import lombok.Setter;

public abstract class EntityRenderer {
	protected ParticleEffectPool effectPool;
	@Getter @Setter private boolean drawReflection = false;
	
	public boolean shouldBeReflected(Entity entity) {
		return true;
	}
	
	public abstract void draw(SpriteBatch batch, Dungeon dungeon, Entity entity);
	
	protected TextureRegion getImageFromSheet(String sheetName, int sheetX, int sheetY) {
		return ImageLoader.getImageFromSheet(sheetName, sheetX, sheetY);
	}
	
	protected void drawEntity(SpriteBatch batch, TextureRegion image, int x, int y) {
		if (image != null) {
			int width = EntityMap.ENTITY_WIDTH;
			int height = EntityMap.ENTITY_HEIGHT;
			
			float ex = x * width + 0.01f;
			float ey = y * height + 0.01f;
			
			if (drawReflection) {
				batch.draw(image, ex, ey + height * 2, 0.0f, 0.0f, width, height, 1.0f, -1.0f, 0.0f);
			} else {
				batch.draw(image, ex, ey);
			}
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
