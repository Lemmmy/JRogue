package jr.rendering.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.containers.EntityItem;
import jr.rendering.entities.animations.EntityAnimationData;
import jr.rendering.items.ItemMap;
import jr.rendering.items.ItemRenderer;
import jr.rendering.tiles.TileMap;

public class EntityRendererItem extends EntityRenderer {
	private static final Matrix4 REFLECTION_MATRIX = new Matrix4();
	
	static {
		REFLECTION_MATRIX.translate(0.0f, TileMap.TILE_HEIGHT, 0.0f);
		REFLECTION_MATRIX.scale(1.0f, -1.0f, 1.0f);
	}
	
	@Override
	public TextureRegion getTextureRegion(Entity entity) {
		EntityItem item = (EntityItem) entity;
		
		if (item.getAppearance() != null) {
			int width = TileMap.TILE_WIDTH;
			int height = TileMap.TILE_HEIGHT;
			
			ItemRenderer renderer = ItemMap.valueOf(item.getItem().getAppearance().name()).getRenderer();
			
			return renderer.getTextureRegion(entity.getDungeon(), item.getItemStack(), item.getItem(), isDrawingReflection());
		}
		
		return null;
	}
	
	@Override
	public void draw(SpriteBatch batch, Entity entity, EntityAnimationData anim, boolean useMemoryLocation) {
		EntityItem item = (EntityItem) entity;
		
		if (item.getAppearance() != null) {
			int width = TileMap.TILE_WIDTH;
			int height = TileMap.TILE_HEIGHT;
			
			ItemRenderer renderer = ItemMap.valueOf(item.getItem().getAppearance().name()).getRenderer();
			
			if (renderer != null) {
				float x = getPositionX(anim, entity, useMemoryLocation);
				float y = getPositionY(anim, entity, useMemoryLocation);
				
				Color oldColour = setAnimationColour(anim, batch, entity);
				
				renderer.draw(
					batch,
					entity.getDungeon(),
					item.getItemStack(),
					item.getItem(),
					(int) (x * width),
					(int) (y * height),
					isDrawingReflection()
				);
				
				batch.setColor(oldColour);
			}
		}
	}
}
