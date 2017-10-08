package jr.rendering.gdxvox.objects.entities.renderers;

import jr.dungeon.entities.Entity;
import jr.rendering.gdxvox.models.magicavoxel.ModelLoader;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.objects.BatchedVoxelModel;
import jr.rendering.gdxvox.objects.entities.EntityRenderer;
import jr.rendering.gdxvox.objects.entities.EntityVoxelBatch;

public class EntityRendererTorch extends EntityRenderer {
	private static VoxelModel torchModel;
	
	public EntityRendererTorch() {
		if (torchModel == null) {
			torchModel = ModelLoader.loadModel("models/entities/torch.vox");
		}
	}
	
	@Override
	public void entityAdded(Entity entity, EntityVoxelBatch batch) {
		BatchedVoxelModel model = new BatchedVoxelModel(torchModel);
		model.setPos(entity.getX(), 0, entity.getY());
		batch.add(entity, model);
	}
}
