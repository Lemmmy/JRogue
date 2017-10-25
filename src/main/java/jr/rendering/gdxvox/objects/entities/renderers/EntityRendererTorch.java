package jr.rendering.gdxvox.objects.entities.renderers;

import com.badlogic.gdx.math.Vector3;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdxvox.lighting.Light;
import jr.rendering.gdxvox.models.magicavoxel.ModelLoader;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.objects.VoxelModelInstance;
import jr.rendering.gdxvox.objects.entities.EntityRenderer;
import jr.rendering.gdxvox.objects.entities.EntityVoxelBatch;
import jr.utils.Point;

public class EntityRendererTorch extends EntityRenderer {
	private static VoxelModel torchModel;
	
	public EntityRendererTorch() {
		if (torchModel == null) {
			torchModel = ModelLoader.loadModel("models/entities/torch.vox");
		}
	}
	
	@Override
	public void entityAdded(Entity entity) {
		float rotation = getRotation(entity.getLevel(), entity.getPosition());
		float dx = (float) Math.sin(Math.toRadians(rotation)) * 0.999f;
		float dy = (float) Math.cos(Math.toRadians(rotation)) * 0.999f;
		// * 0.999f here slightly extrudes them from the wall in case they are overlapping with a brick
		
		manager.getStaticBatch(entity.getX(), entity.getY())
			.add(entity, new VoxelModelInstance(torchModel)
			.setOffset(-dx, 0, dy)
			.setRotation(rotation));
		
		if (entity instanceof LightEmitter) {
			LightEmitter le = (LightEmitter) entity;
			
			Light light = new Light(
				le.isLightEnabled(),
				new Vector3(entity.getX(), 0, entity.getY()),
				new Vector3(-dx * 0.9f, 0.65f, dy * 0.9f),
				le.getLightColour(),
				le.getLightAttenuationFactor()
			);
			
			scene.lightContext.addLight(entity, light);
		}
	}
	
	private float getRotation(Level level, Point position) {
		TileType[] adjacentTiles = level.tileStore.getAdjacentTileTypes(position);
		
		if (adjacentTiles[0].isWallTile()) return 270;
		else if (adjacentTiles[1].isWallTile()) return 90;
		else if (adjacentTiles[2].isWallTile()) return 0;
		else if (adjacentTiles[3].isWallTile()) return 180;
		
		return 0;
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
