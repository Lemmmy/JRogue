package jr.rendering.gdxvox.objects.tiles;

import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.gdxvox.objects.AbstractObjectRenderer;

public abstract class TileRenderer extends AbstractObjectRenderer<TileType, Tile, TileVoxelBatch> {
	public static final int TILE_WIDTH = 16;
	public static final int TILE_HEIGHT = 16;
	public static final int TILE_DEPTH = 16;
	
	@Override
	public void initialiseBatch() {
		setBatch(new TileVoxelBatch());
	}
	
	@Override
	public void objectAdded(Tile object, SceneContext scene) {
		if (getBatch().contains(object)) return;
		tileAdded(object, getBatch(), scene);
	}
	
	public abstract void tileAdded(Tile tile, TileVoxelBatch batch, SceneContext scene);
}
