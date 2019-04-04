package jr.rendering.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.rendering.entities.animations.EntityAnimationData;

public class EntityRendererFish extends EntityRendererBasic {
	private static final float FISH_ALPHA = 0.7f;
	
	public EntityRendererFish(String fileName) {
		super(fileName);
	}
	
	@Override
	public boolean shouldBeReflected(Entity entity) {
		return false;
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
