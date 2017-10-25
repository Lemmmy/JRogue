package jr.rendering.gdxvox.objects.entities;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.rendering.gdxvox.objects.AbstractObjectRenderer;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityRenderer extends AbstractObjectRenderer<EntityAppearance, Entity, EntityVoxelBatch, EntityRendererManager> {
	public static final int ENTITY_WIDTH = TileRenderer.TILE_WIDTH;
	public static final int ENTITY_HEIGHT = TileRenderer.TILE_HEIGHT;
	public static final int ENTITY_DEPTH = TileRenderer.TILE_DEPTH;
	
	private List<Entity> entities = new ArrayList<>();
	
	@Override
	public void objectAdded(Entity object) {
		if (manager.getBatchContainingObject(object).isPresent()) return;
		entities.add(object);
		entityAdded(object);
	}
	
	public abstract void entityAdded(Entity entity);
	
	@Override
	public void objectRemoved(Entity object) {
		manager.removeObjectInstance(object);
		entities.remove(object);
		entityRemoved(object);
	}
	
	public void entityRemoved(Entity entity) {}
	
	public void entityMoved(Entity entity, EntityMovedEvent event) {
		manager.getBatchContainingObject(entity).ifPresent(batch -> batch.move(entity));
	}
}
