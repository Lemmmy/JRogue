package jr.rendering.gdxvox.objects.entities.renderers;

import com.badlogic.gdx.math.Vector3;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.rendering.gdxvox.models.magicavoxel.ModelLoader;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.objects.VoxelModelInstance;
import jr.rendering.gdxvox.objects.entities.EntityRenderer;
import jr.rendering.gdxvox.objects.entities.EntityVoxelBatch;
import jr.rendering.gdxvox.utils.Light;
import jr.rendering.gdxvox.utils.SceneContext;

public class EntityRendererTorch extends EntityRenderer {
	private static VoxelModel torchModel;
	
	public EntityRendererTorch() {
		if (torchModel == null) {
			torchModel = ModelLoader.loadModel("models/entities/torch.vox");
		}
	}
	
	@Override
	public void entityAdded(Entity entity, EntityVoxelBatch batch, SceneContext scene) {
		batch.add(entity, new VoxelModelInstance(torchModel));
		
		if (entity instanceof LightEmitter) {
			LightEmitter le = (LightEmitter) entity;
			
			Light light = new Light(
				le.isLightEnabled(),
				new Vector3(entity.getX(), 0, entity.getY()),
				new Vector3(-0.5f, 0.5f, -0.5f),
				le.getLightColour(),
				le.getLightAttenuationFactor()
			);
			
			scene.addLight(entity, light);
		}
	}
	
	@Override
	public void entityRemoved(Entity entity, EntityVoxelBatch batch, SceneContext scene) {
		scene.removeLight(entity);
	}
	
	@Override
	public void entityMoved(Entity entity, EntityMovedEvent event, EntityVoxelBatch batch, SceneContext scene) {
		scene.moveLight(entity);
	}
}
