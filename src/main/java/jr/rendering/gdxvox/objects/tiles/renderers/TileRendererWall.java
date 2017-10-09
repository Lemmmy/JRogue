package jr.rendering.gdxvox.objects.tiles.renderers;

import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdx2d.utils.BlobUtils;
import jr.rendering.gdxvox.models.magicavoxel.ModelLoader;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.objects.VoxelModelInstance;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;
import jr.rendering.gdxvox.objects.tiles.TileVoxelBatch;
import jr.rendering.gdxvox.utils.SceneContext;

public class TileRendererWall extends TileRenderer {
	private static final WallModel[] MAP = new WallModel[] {
		null,
		new WallModel("wall-end", 180),
		new WallModel("wall-end", 270),
		new WallModel("wall-corner", 0),
		new WallModel("wall-end", 0),
		new WallModel("wall", 0),
		new WallModel("wall-corner", 90),
		new WallModel("wall-t", 0),
		new WallModel("wall-end", 90),
		new WallModel("wall-corner", 270),
		new WallModel("wall", 90),
		new WallModel("wall-t", 270),
		new WallModel("wall-corner", 180),
		new WallModel("wall-t", 180),
		new WallModel("wall-t", 90),
		new WallModel("wall-x", 0)
	};
	
	private static boolean loadedMap;
	
	public TileRendererWall() {
		if (!loadedMap) {
			loadedMap = true;
			
			for (WallModel wallModel : MAP) {
				if (wallModel == null) continue;
				wallModel.loadModel();
			}
		}
	}
	
	protected int getPositionMask(Tile tile) {
		return BlobUtils.getPositionMask4(this::isJoinedTile, tile.getLevel(), tile.getX(), tile.getY());
	}
	
	protected boolean isJoinedTile(TileType type) {
		return type != null && type.isWallTile();
	}
	
	@Override
	public void tileAdded(Tile tile, TileVoxelBatch batch, SceneContext scene) {
		int x = tile.getX();
		int y = tile.getY();
		
		WallModel wallModel = MAP[getPositionMask(tile)];
		if (wallModel == null || wallModel.model == null) return;
		
		batch.add(tile, new VoxelModelInstance(wallModel.model)
			.setRotation(wallModel.rotation));
	}
	
	protected static class WallModel {
		private String modelName;
		private float rotation;
		private VoxelModel model;
		
		public WallModel(String modelName, float rotation) {
			this.modelName = "models/tiles/" + modelName + ".vox";
			this.rotation = rotation;
		}
		
		protected void loadModel() {
			model = ModelLoader.loadModel(this.modelName);
		}
	}
}
