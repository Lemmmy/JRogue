package jr.rendering.gdxvox.objects.entities.renderers;

import jr.dungeon.entities.Entity;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.gdxvox.models.magicavoxel.ModelLoader;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.objects.VoxelModelInstance;
import jr.rendering.gdxvox.objects.entities.EntityRenderer;
import jr.rendering.gdxvox.objects.entities.EntityVoxelBatch;

public class EntityRendererPlayer extends EntityRenderer {
	private static VoxelModel playerModel;
	
	public EntityRendererPlayer() {
		if (playerModel == null) {
			playerModel = ModelLoader.loadModel("models/classes/wizard/wizard.vox"); // TODO: role models (ha ha)
		}
	}
	
	@Override
	public void entityAdded(Entity entity, EntityVoxelBatch batch, SceneContext scene) {
		batch.add(entity, new VoxelModelInstance(playerModel));
		
		// TODO: light emission
	}
}
