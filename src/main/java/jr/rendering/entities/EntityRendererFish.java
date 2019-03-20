package jr.rendering.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;

public class EntityRendererFish extends EntityRenderer {
	private static final float FISH_ALPHA = 0.7f;
	
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
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity, boolean useMemoryLocation) {
		float x = getPositionX(entity, useMemoryLocation);
		float y = getPositionY(entity, useMemoryLocation);
		
		Color oldColour = setAnimationColour(batch, entity, FISH_ALPHA);
		drawEntity(batch, image, x, y);
		batch.setColor(oldColour);
	}
}
