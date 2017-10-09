package jr.rendering.gdxvox.objects.tiles.renderers;

import jr.dungeon.tiles.Tile;
import jr.rendering.gdxvox.models.magicavoxel.ModelLoader;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.objects.VoxelModelInstance;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;
import jr.rendering.gdxvox.objects.tiles.TileVoxelBatch;
import jr.rendering.gdxvox.utils.SceneContext;

public class TileRendererFloor extends TileRenderer {
	private static VoxelModel torchModel;
	
	public TileRendererFloor() {
		if (torchModel == null) {
			torchModel = ModelLoader.loadModel("models/tiles/floor.vox");
		}
	}
	
	@Override
	public void tileAdded(Tile tile, TileVoxelBatch batch, SceneContext scene) {
		batch.add(tile, new VoxelModelInstance(torchModel)
			.setOffset(0, 0 - 1f / 16f, 0));
	}
}
