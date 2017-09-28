package jr.rendering.gdx2d.entities;

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
		float x = entity.getLastSeenX() + getAnimationFloat(entity, "offsetX", 0);
		float y = entity.getLastSeenY() + getAnimationFloat(entity, "offsetY", 0);
		
		float[] ac = getAnimationColour(entity);
		
		Color c = batch.getColor();
		batch.setColor(c.r * ac[0], c.g * ac[1], c.b * ac[2], c.a * ac[3] * 0.7f);
		drawEntity(batch, image, x, y);
		batch.setColor(c);
	}
}
