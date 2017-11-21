package jr.rendering.gdx2d.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;

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
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity, boolean useMemoryLocation) {
		float x = getPositionX(entity, useMemoryLocation);
		float y = getPositionY(entity, useMemoryLocation);
		
		float[] ac = getAnimationColour(entity);
		
		Color c = batch.getColor();
		batch.setColor(c.r * ac[0], c.g * ac[1], c.b * ac[2], c.a * ac[3]);
		drawEntity(batch, getTextureRegion(dungeon, entity), x, y);
		batch.setColor(c);
	}
}
