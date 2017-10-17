package jr.rendering.gdx2d.entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.generators.Climate;
import jr.rendering.utils.ImageLoader;
import lombok.Getter;
import lombok.Setter;

public abstract class EntityRenderer {
	protected ParticleEffectPool effectPool;
	@Getter @Setter private boolean drawingReflection = false;
	
	public boolean shouldBeReflected(Entity entity) {
		return true;
	}

	public boolean shouldRenderReal(Entity entity) {
		return entity.getLevel().getClimate() != Climate.__;
	}

	public abstract TextureRegion getTextureRegion(Dungeon dungeon, Entity entity);
	
	public abstract void draw(SpriteBatch batch, Dungeon dungeon, Entity entity, boolean useMemoryLocation);
	
	protected TextureRegion getImageFromSheet(String sheetName, int sheetX, int sheetY) {
		return ImageLoader.getImageFromSheet(sheetName, sheetX, sheetY);
	}
	
	protected void drawEntity(SpriteBatch batch, TextureRegion image, float x, float y) {
		if (image != null) {
			int width = EntityMap.ENTITY_WIDTH;
			int height = EntityMap.ENTITY_HEIGHT;
			
			float ex = x * width + 0.01f;
			float ey = y * height + 0.01f;
			
			if (drawingReflection) {
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
	
	public float getAnimationFloat(Entity entity, String name, float def) {
		if (!entity.getPersistence().has("animationData")) return def;
		
		return (float) entity.getPersistence().getJSONObject("animationData").optDouble(name, (double) def);
	}
	
	public float[] getAnimationColour(Entity entity) {
		return new float[] {
			getAnimationFloat(entity, "r", 1),
			getAnimationFloat(entity, "g", 1),
			getAnimationFloat(entity, "b", 1),
			getAnimationFloat(entity, "a", 1)
		};
	}
	
	public float getPositionX(Entity entity, boolean useMemoryLocation) {
		return (useMemoryLocation ? entity.getLastSeenX() : entity.getX()) +
			getAnimationFloat(entity, "offsetX", 0);
	}
	
	public float getPositionY(Entity entity, boolean useMemoryLocation) {
		return (useMemoryLocation ? entity.getLastSeenY() : entity.getY()) +
			getAnimationFloat(entity, "offsetY", 0);
	}
}
