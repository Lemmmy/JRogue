package jr.rendering.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.projectiles.EntityProjectile;
import jr.rendering.entities.animations.EntityAnimationData;

public class EntityRendererProjectile extends EntityRenderer {
	protected TextureRegion image;
	
	public EntityRendererProjectile(String sheetName, int sheetX, int sheetY) {
		image = getImageFromSheet(sheetName, sheetX, sheetY);
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, Entity entity) {
		return image;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity, EntityAnimationData anim, boolean useMemoryLocation) {
		int width = EntityMap.ENTITY_WIDTH;
		int height = EntityMap.ENTITY_HEIGHT;
		float worldX = getPositionX(anim, entity, useMemoryLocation);
		float worldY = getPositionY(anim, entity, useMemoryLocation);
		float x = worldX * width;
		float y = worldY * height;
		float originX = width / 2f;
		float originY = height / 2f;
		float rotation = 0;
		
		if (entity instanceof EntityProjectile) {
			EntityProjectile projectile = (EntityProjectile) entity;
			rotation = (float) (Math.atan2(projectile.getDeltaY(), projectile.getDeltaX()) * (180 / Math.PI));
		}
		
		Color oldColour = setAnimationColour(anim, batch, entity);
		
		if (isDrawingReflection()) {
			batch.draw(
				getTextureRegion(dungeon, entity),
				x, y + height,
				originX, originY,
				width, height,
				1, -1,
				rotation
			);
		} else {
			batch.draw(
				getTextureRegion(dungeon, entity),
				x, y,
				originX, originY,
				width, height,
				1, 1,
				rotation
			);
		}
		
		batch.setColor(oldColour);
	}
}
