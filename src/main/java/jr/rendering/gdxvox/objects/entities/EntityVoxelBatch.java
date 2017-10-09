package jr.rendering.gdxvox.objects.entities;

import jr.dungeon.entities.Entity;
import jr.dungeon.tiles.Tile;
import jr.rendering.gdxvox.objects.VoxelBatch;
import jr.rendering.gdxvox.objects.VoxelModelInstance;

public class EntityVoxelBatch extends VoxelBatch<Entity> {
	@Override
	protected void setAddedObjectPosition(Entity entity, VoxelModelInstance model) {
		model.setPosition(entity.getX(), 0, entity.getY());
	}
}
