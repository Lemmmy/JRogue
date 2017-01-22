package jr.rendering.gdx.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.containers.EntityItem;
import jr.rendering.gdx.items.ItemMap;
import jr.rendering.gdx.items.ItemRenderer;
import jr.rendering.gdx.tiles.TileMap;

public class EntityRendererItem extends EntityRenderer {
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity) {
		EntityItem item = (EntityItem) entity;
		
		if (item.getAppearance() != null) {
			int width = TileMap.TILE_WIDTH;
			int height = TileMap.TILE_HEIGHT;
			
			ItemRenderer renderer = ItemMap.valueOf(item.getItem().getAppearance().name()).getRenderer();
			
			if (renderer != null) {
				renderer.draw(
					batch,
					dungeon,
					item.getItemStack(),
					item.getItem(),
					entity.getLastSeenX() * width,
					entity.getLastSeenY() * height
				);
			}
		}
	}
}
