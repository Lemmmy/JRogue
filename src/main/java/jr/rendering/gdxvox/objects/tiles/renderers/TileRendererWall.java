package jr.rendering.gdxvox.objects.tiles.renderers;

import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import jr.dungeon.Level;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdx2d.utils.BlobUtils;
import jr.rendering.gdxvox.models.magicavoxel.FramedModel;
import jr.rendering.gdxvox.models.magicavoxel.ModelConverter;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;
import jr.rendering.gdxvox.objects.tiles.TileRendererInstance;

public class TileRendererWall extends TileRenderer {
	private static final WallModel[] MAP = new WallModel[] {
		null,
		new WallModel("wall-end", 0),
		new WallModel("wall-end", 0),
		new WallModel("wall-corner", 0),
		new WallModel("wall-end", 0),
		new WallModel("wall", 0),
		new WallModel("wall-corner", 0),
		new WallModel("wall-t", 0),
		new WallModel("wall-end", 0),
		new WallModel("wall-corner", 0),
		new WallModel("wall", 90),
		new WallModel("wall-t", 0),
		new WallModel("wall-corner", 0),
		new WallModel("wall-t", 0),
		new WallModel("wall-t", 0),
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
	
	@SuppressWarnings("Duplicates")
	protected int getPositionMask(Tile tile) {
		return BlobUtils.getPositionMask4(this::isJoinedTile, tile.getLevel(), tile.getX(), tile.getY());
	}
	
	protected boolean isJoinedTile(TileType type) {
		return type.isWallTile();
	}
	
	@Override
	public void tileAdded(Tile tile) {
		int x = tile.getX();
		int y = tile.getY();
		
		WallModel model = MAP[getPositionMask(tile)];
		
		ModelInstance instance = new ModelInstance(model.model);
		instance.transform.translate(x, 0, y);
		instance.transform.translate(0.5f, 0, 0.5f);
		instance.transform.rotate(0, 1, 0, model.rotation);
		instance.transform.translate(-0.5f, 0, -0.5f);
		objectInstanceMap.put(tile, new TileRendererInstance(tile, instance));
	}
	
	@Override
	public boolean shouldDraw(TileRendererInstance instance) {
		return true;
	}
	
	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		objectInstanceMap.values().forEach(instance -> instance.getModelInstance().getRenderables(renderables, pool));
	}
	
	protected static class WallModel {
		private String modelName;
		private float rotation;
		private Model model;
		
		public WallModel(String modelName, float rotation) {
			this.modelName = "models/tiles/" + modelName + ".vox";
			this.rotation = rotation;
		}
		
		protected void loadModel() {
			FramedModel model = ModelConverter.loadModel(this.modelName);
			if (model == null) return;
			
			this.model = model.getFrames()[0];
		}
	}
}
