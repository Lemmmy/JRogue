package jr.rendering.gdxvox.objects.entities;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.dungeon.events.EventHandler;
import jr.dungeon.tiles.Tile;
import jr.rendering.gdxvox.objects.AbstractObjectRenderer;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;
import jr.rendering.gdxvox.utils.SceneContext;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityRenderer extends AbstractObjectRenderer<EntityAppearance, Entity, EntityVoxelBatch> {
	public static final int ENTITY_WIDTH = TileRenderer.TILE_WIDTH;
	public static final int ENTITY_HEIGHT = TileRenderer.TILE_HEIGHT;
	public static final int ENTITY_DEPTH = TileRenderer.TILE_DEPTH;
	
	private List<Entity> entities = new ArrayList<>();
	
	@Override
	public void initialiseBatch() {
		setBatch(new EntityVoxelBatch());
	}
	
	@Override
	public void objectAdded(Entity object, SceneContext scene) {
		if (getBatch().contains(object)) return;
		entities.add(object);
		entityAdded(object, getBatch(), scene);
	}
	
	public abstract void entityAdded(Entity entity, EntityVoxelBatch batch, SceneContext scene);
	
	@Override
	public void objectRemoved(Entity object, SceneContext scene) {
		getBatch().remove(object);
		entities.remove(object);
		entityRemoved(object, getBatch(), scene);
	}
	
	public void entityRemoved(Entity entity, EntityVoxelBatch batch, SceneContext scene) {}
	
	public void entityMoved(Entity entity, EntityMovedEvent event, EntityVoxelBatch batch, SceneContext scene) {}
}
