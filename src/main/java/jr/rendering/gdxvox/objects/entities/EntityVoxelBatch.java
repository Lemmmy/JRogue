package jr.rendering.gdxvox.objects.entities;

import jr.dungeon.entities.Entity;
import jr.rendering.gdxvox.objects.VoxelBatch;
import jr.rendering.gdxvox.objects.VoxelModelInstance;

public class EntityVoxelBatch extends VoxelBatch<Entity> {
	@Override
	protected void updateObjectPosition(Entity entity, VoxelModelInstance model) {
		model.setPosition(entity.getX(), 0, entity.getY());
	}
}
