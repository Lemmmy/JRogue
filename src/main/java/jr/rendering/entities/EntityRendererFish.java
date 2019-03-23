package jr.rendering.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.rendering.entities.animations.EntityAnimationData;

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
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity, EntityAnimationData anim, boolean useMemoryLocation) {
		float x = getPositionX(anim, entity, useMemoryLocation);
		float y = getPositionY(anim, entity, useMemoryLocation);
		
		Color oldColour = setAnimationColour(anim, batch, entity, FISH_ALPHA);
		drawEntity(batch, image, x, y);
		batch.setColor(oldColour);
	}
}
