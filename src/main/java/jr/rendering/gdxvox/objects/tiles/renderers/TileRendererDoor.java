package jr.rendering.gdxvox.objects.tiles.renderers;

import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdxvox.models.magicavoxel.ModelLoader;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.objects.VoxelModelInstance;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;
import jr.rendering.gdxvox.objects.tiles.TileVoxelBatch;
import jr.rendering.gdxvox.utils.SceneContext;

public class TileRendererDoor extends TileRenderer {
	private VoxelModel doorModel;
	
	public TileRendererDoor() {
		doorModel = ModelLoader.loadModel("models/tiles/door.vox");
	}
	
	@Override
	public void tileAdded(Tile tile, TileVoxelBatch batch, SceneContext scene) {
		int x = tile.getX();
		int y = tile.getY();
		
		TileType[] adjacentTiles = tile.getLevel().tileStore.getAdjacentTileTypes(x, y);
		
		boolean h = adjacentTiles[0].isWallTile() || adjacentTiles[1].isWallTile();
		float rotation = h ? 90 : 0;
		
		VoxelModelInstance instance = new VoxelModelInstance(doorModel)
			.setRotation(rotation)
			.setPos(x, 0, y);
		
		batch.add(tile, instance);
	}
}
