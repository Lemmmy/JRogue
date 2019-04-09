package jr.rendering.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.entities.Entity;
import jr.rendering.assets.Assets;
import jr.rendering.entities.animations.EntityAnimationData;

import static jr.rendering.assets.Textures.entityFile;

public class EntityRendererBasic extends EntityRenderer {
	protected TextureRegion image;
	private String fileName;
	
	public EntityRendererBasic(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		if (fileName != null) {
			assets.textures.loadPacked(entityFile(fileName), t -> image = t);
		}
	}
	
	@Override
	public TextureRegion getTextureRegion(Entity entity) {
		return image;
	}
	
	@Override
	public void draw(SpriteBatch batch, Entity entity, EntityAnimationData anim, boolean useMemoryLocation) {
		float x = getPositionX(anim, entity, useMemoryLocation);
		float y = getPositionY(anim, entity, useMemoryLocation);
		
		Color oldColour = setAnimationColour(anim, batch, entity);
		drawEntity(batch, getTextureRegion(entity), x, y);
		batch.setColor(oldColour);
	}
}
