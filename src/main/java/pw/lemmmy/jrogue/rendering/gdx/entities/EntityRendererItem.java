package pw.lemmmy.jrogue.rendering.gdx.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityItem;
import pw.lemmmy.jrogue.rendering.gdx.items.ItemMap;
import pw.lemmmy.jrogue.rendering.gdx.items.ItemRenderer;
import pw.lemmmy.jrogue.rendering.gdx.tiles.TileMap;

public class EntityRendererItem extends EntityRenderer {
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, Entity entity) {
		EntityItem item = (EntityItem) entity;

		if (item.getAppearance() != null) {
			int width = TileMap.TILE_WIDTH;
			int height = TileMap.TILE_HEIGHT;

			ItemRenderer renderer = ItemMap.valueOf(item.getItem().getAppearance().name()).getRenderer();

			if (renderer != null) {
				renderer.draw(batch, dungeon, item.getItem(), entity.getX() * width, entity.getY() * height);
			}
		}
	}
}
