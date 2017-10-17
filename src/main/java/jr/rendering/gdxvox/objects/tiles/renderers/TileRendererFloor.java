package jr.rendering.gdxvox.objects.tiles.renderers;

import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdxvox.models.magicavoxel.ModelLoader;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.objects.VoxelModelInstance;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;
import jr.rendering.gdxvox.objects.tiles.TileVoxelBatch;
import jr.rendering.gdxvox.context.SceneContext;

public class TileRendererFloor extends TileRenderer {
	private static VoxelModel floorModel, floorCorner, floorH, floorV;
	
	private static final float FLOOR_Y = 0 - 1f / TileRenderer.TILE_HEIGHT;
	
	public TileRendererFloor() {
		if (floorModel == null) {
			floorModel = ModelLoader.loadModel("models/tiles/floor/floor.vox");
			floorCorner = ModelLoader.loadModel("models/tiles/floor/floor-corner.vox");
			floorH = ModelLoader.loadModel("models/tiles/floor/floor-h.vox");
			floorV = ModelLoader.loadModel("models/tiles/floor/floor-v.vox");
		}
	}
	
	@Override
	public void tileAdded(Tile tile, TileVoxelBatch batch, SceneContext scene) {
		TileType[] adjacentTiles = tile.getLevel().tileStore.getAdjacentTileTypes(tile.getPosition());
		
		batch.add(tile, new VoxelModelInstance(floorModel)
			.setOffset(0, 0 - 1f / 16f, 0));
		
		addHalfFloors(tile, batch, adjacentTiles);
		addCornerFloors(tile, batch, adjacentTiles);
	}
	
	private void addHalfFloors(Tile tile, TileVoxelBatch batch, TileType[] adjacentTiles) {
		// TODO: might need a different check here? e.g. isHalfTile
		// TODO: this is a disaster
		if (adjacentTiles[0].isWallTile())
			batch.add(tile, new VoxelModelInstance(floorV)
				.setOffset(0.5f, FLOOR_Y, 0));
		
		if (adjacentTiles[1].isWallTile())
			batch.add(tile, new VoxelModelInstance(floorV)
				.setOffset(-1.0f, FLOOR_Y, 0));
		
		if (adjacentTiles[2].isWallTile())
			batch.add(tile, new VoxelModelInstance(floorH)
				.setOffset(0, FLOOR_Y, 0.5f));
		
		if (adjacentTiles[3].isWallTile())
			batch.add(tile, new VoxelModelInstance(floorH)
				.setOffset(0, FLOOR_Y, -1.0f));
	}
	
	private void addCornerFloors(Tile tile, TileVoxelBatch batch, TileType[] adjacentTiles) {
		if (adjacentTiles[0].isWallTile() && adjacentTiles[2].isWallTile())
			batch.add(tile, new VoxelModelInstance(floorCorner)
				.setOffset(0.5f, FLOOR_Y, 0.5f));
		
		if (adjacentTiles[1].isWallTile() && adjacentTiles[3].isWallTile())
			batch.add(tile, new VoxelModelInstance(floorCorner)
				.setOffset(-1.0f, FLOOR_Y, -1.0f));
			
		if (adjacentTiles[1].isWallTile() && adjacentTiles[2].isWallTile())
			batch.add(tile, new VoxelModelInstance(floorCorner)
				.setOffset(-1.0f, FLOOR_Y, 0.5f));
		
		if (adjacentTiles[0].isWallTile() && adjacentTiles[3].isWallTile())
			batch.add(tile, new VoxelModelInstance(floorCorner)
				.setOffset(0.5f, FLOOR_Y, -1.0f));
	}
}
