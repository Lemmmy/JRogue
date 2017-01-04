package pw.lemmmy.jrogue.rendering.gdx.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.projectiles.EntityProjectile;

public class EntityRendererProjectile extends EntityRenderer {
	protected TextureRegion image;
	
	public EntityRendererProjectile(String sheetName, int sheetX, int sheetY) {
		image = getImageFromSheet(sheetName, sheetX, sheetY);
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
		
		batch.draw(image, x, y, originX, originY, width, height, 1, 1, rotation);
	}
}
