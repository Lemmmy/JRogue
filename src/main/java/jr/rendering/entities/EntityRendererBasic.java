package jr.rendering.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.rendering.entities.animations.EntityAnimationData;

public class EntityRendererBasic extends EntityRenderer {
	protected TextureRegion image;
	
	public EntityRendererBasic(String sheetName, int sheetX, int sheetY) {
		image = getImageFromSheet(sheetName, sheetX, sheetY);
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, Entity entity) {
		return image;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity, EntityAnimationData anim, boolean useMemoryLocation) {
		float x = getPositionX(anim, entity, useMemoryLocation);
		float y = getPositionY(anim, entity, useMemoryLocation);
		
		Color oldColour = setAnimationColour(anim, batch, entity);
		drawEntity(batch, getTextureRegion(dungeon, entity), x, y);
		batch.setColor(oldColour);
	}
}
