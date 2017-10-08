package jr.rendering.gdxvox.objects.entities;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.tiles.Tile;
import jr.rendering.gdxvox.objects.AbstractObjectRenderer;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;

public abstract class EntityRenderer extends AbstractObjectRenderer<EntityAppearance, Entity, EntityVoxelBatch> {
	public static final int ENTITY_WIDTH = TileRenderer.TILE_WIDTH;
	public static final int ENTITY_HEIGHT = TileRenderer.TILE_HEIGHT;
	public static final int ENTITY_DEPTH = TileRenderer.TILE_DEPTH;
	
	@Override
	public void initialiseBatch() {
		setBatch(new EntityVoxelBatch());
	}
	
	@Override
	public void objectAdded(Entity object) {
		if (getBatch().contains(object)) return;
		entityAdded(object, getBatch());
	}
	
	public abstract void entityAdded(Entity entity, EntityVoxelBatch batch);
}
