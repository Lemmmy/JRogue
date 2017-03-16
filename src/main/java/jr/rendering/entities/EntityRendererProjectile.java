package jr.rendering.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.projectiles.EntityProjectile;

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
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity) {
		int worldX = entity.getLastSeenX();
		int worldY = entity.getLastSeenY();
		int width = EntityMap.ENTITY_WIDTH;
		int height = EntityMap.ENTITY_HEIGHT;
		int x = worldX * width;
		int y = worldY * height;
		int originX = width / 2;
		int originY = height / 2;
		float rotation = 0;
		
		if (entity instanceof EntityProjectile) {
			int dx = ((EntityProjectile) entity).getDeltaX();
			int dy = ((EntityProjectile) entity).getDeltaY();
			
			rotation = (float) (Math.atan2(dy, dx) * (180 / Math.PI));
		}
		
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
	}
}
