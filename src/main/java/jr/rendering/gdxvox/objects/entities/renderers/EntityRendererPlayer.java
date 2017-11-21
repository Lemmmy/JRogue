package jr.rendering.gdxvox.objects.entities.renderers;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.rendering.gdxvox.models.magicavoxel.ModelLoader;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.objects.VoxelModelInstance;
import jr.rendering.gdxvox.objects.entities.EntityRenderer;

public class EntityRendererPlayer extends EntityRenderer {
	private static VoxelModel playerModel;
	
	public EntityRendererPlayer() {
		if (playerModel == null) {
			playerModel = ModelLoader.loadModel("models/classes/wizard/wizard.vox"); // TODO: role models (ha ha)
		}
	}
	
	@Override
	public void entityAdded(Entity entity) {
		manager.getDynamicBatch().add(entity, new VoxelModelInstance(playerModel));
		
		// TODO: light emission
	}
	
	@Override
	public void entityMoved(Entity entity, EntityMovedEvent event) {
		super.entityMoved(entity, event);
		
		float angle = (float) Math.toDegrees(Math.atan2(event.getDeltaY(), event.getDeltaX())) + 90;
		
		manager.getDynamicBatch().getInstance(entity).ifPresent(instance -> instance.setRotation(angle));
	}
}