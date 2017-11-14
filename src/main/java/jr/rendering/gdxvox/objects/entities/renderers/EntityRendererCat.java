package jr.rendering.gdxvox.objects.entities.renderers;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.rendering.gdxvox.models.magicavoxel.ModelLoader;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.objects.VoxelModelInstance;
import jr.rendering.gdxvox.objects.entities.EntityRenderer;

public class EntityRendererCat extends EntityRenderer {
	private static VoxelModel catModel;
	
	public EntityRendererCat() {
		if (catModel == null) {
			catModel = ModelLoader.loadModel("models/entities/cat.vox");
		}
	}
	
	@Override
	public void entityAdded(Entity entity) {
		manager.getDynamicBatch().add(entity, new VoxelModelInstance(catModel));
	}
	
	@Override
	public void entityMoved(Entity entity, EntityMovedEvent event) {
		super.entityMoved(entity, event);
		
		float angle = (float) Math.toDegrees(Math.atan2(event.getDeltaY(), event.getDeltaX())) + 90;
		
		manager.getDynamicBatch().getInstance(entity).ifPresent(instance -> instance.setRotation(angle));
	}
}