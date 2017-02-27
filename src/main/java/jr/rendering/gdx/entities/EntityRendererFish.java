package jr.rendering.gdx.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;

public class EntityRendererFish extends EntityRenderer {
	protected TextureRegion image;
	
	public EntityRendererFish(int sheetX, int sheetY) {
		image = getImageFromSheet("textures/entities.png", sheetX, sheetY);
	}
	
	@Override
	public boolean shouldBeReflected(Entity entity) {
		return false;
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, Entity entity) {
		return image;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity) {
		float x = entity.getLastSeenX() + (float) entity.getPersistence().optDouble("lerpX", 0);
		float y = entity.getLastSeenY() + (float) entity.getPersistence().optDouble("lerpY", 0);
		
		Color c = batch.getColor();
		batch.setColor(c.r, c.g, c.b, 0.7f);
		drawEntity(batch, image, x, y);
		batch.setColor(c);
	}
}
