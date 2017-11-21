package jr.rendering.gdx2d.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.containers.EntityItem;
import jr.rendering.gdx2d.items.ItemMap;
import jr.rendering.gdx2d.items.ItemRenderer;
import jr.rendering.gdx2d.tiles.TileMap;

public class EntityRendererItem extends EntityRenderer {
	private static final Matrix4 REFLECTION_MATRIX = new Matrix4();
	
	static {
		REFLECTION_MATRIX.translate(0.0f, TileMap.TILE_HEIGHT, 0.0f);
		REFLECTION_MATRIX.scale(1.0f, -1.0f, 1.0f);
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, Entity entity) {
		EntityItem item = (EntityItem) entity;
		
		if (item.getAppearance() != null) {
			int width = TileMap.TILE_WIDTH;
			int height = TileMap.TILE_HEIGHT;
			
			ItemRenderer renderer = ItemMap.valueOf(item.getItem().getAppearance().name()).getRenderer();
			
			return renderer.getTextureRegion(dungeon, item.getItemStack(), item.getItem(), isDrawingReflection());
		}
		
		return null;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity, boolean useMemoryLocation) {
		EntityItem item = (EntityItem) entity;
		
		if (item.getAppearance() != null) {
			int width = TileMap.TILE_WIDTH;
			int height = TileMap.TILE_HEIGHT;
			
			ItemRenderer renderer = ItemMap.valueOf(item.getItem().getAppearance().name()).getRenderer();
			
			if (renderer != null) {
				float x = getPositionX(entity, useMemoryLocation);
				float y = getPositionY(entity, useMemoryLocation);
				
				float[] ac = getAnimationColour(entity);
				
				Color c = batch.getColor();
				batch.setColor(c.r * ac[0], c.g * ac[1], c.b * ac[2], c.a * ac[3]);
				
				renderer.draw(
					batch,
					dungeon,
					item.getItemStack(),
					item.getItem(),
					(int) (x * width),
					(int) (y * height),
					isDrawingReflection()
				);
				
				batch.setColor(c);
			}
		}
	}
}
