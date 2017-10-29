package jr.rendering.gdxvox.objects.entities.renderers;

import com.badlogic.gdx.math.Vector3;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.rendering.gdxvox.lighting.Light;
import jr.rendering.gdxvox.models.magicavoxel.ModelLoader;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.objects.VoxelModelInstance;
import jr.rendering.gdxvox.objects.entities.EntityRenderer;

public class EntityRendererCandlestick extends EntityRenderer {
	private static VoxelModel candlestickModel;
	
	public EntityRendererCandlestick() {
		if (candlestickModel == null) {
			candlestickModel = ModelLoader.loadModel("models/entities/candlestick.vox");
		}
	}
	
	@Override
	public void entityAdded(Entity entity) {
		manager.getDynamicBatch().add(entity, new VoxelModelInstance(candlestickModel));
		
		if (entity instanceof LightEmitter) {
			LightEmitter le = (LightEmitter) entity;
			
			Light light = new Light(
				le.isLightEnabled(),
				new Vector3(entity.getX(), 0, entity.getY()),
				new Vector3(-0.5f, 0.5f, -0.5f),
				le.getLightColour(),
				le.getLightAttenuationFactor()
			);
			
			scene.lightContext.addLight(entity, light);
		}
	}
	
	@Override
	public void entityRemoved(Entity entity) {
		scene.lightContext.removeLight(entity);
	}
	
	@Override
	public void entityMoved(Entity entity, EntityMovedEvent event) {
		scene.lightContext.moveLight(entity);
	}
}
