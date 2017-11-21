package jr.rendering.gdxvox.objects.tiles;

import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdxvox.objects.AbstractObjectRenderer;

public abstract class TileRenderer extends AbstractObjectRenderer<TileType, Tile, TileVoxelBatch, TileRendererManager> {
	public static final int TILE_WIDTH = 16;
	public static final int TILE_HEIGHT = 16;
	public static final int TILE_DEPTH = 16;
	
	@Override
	public void objectAdded(Tile object) {
		if (manager.getBatchContainingObject(object).isPresent()) return;
		tileAdded(object);
	}
	
	public abstract void tileAdded(Tile tile);
	
	@Override
	public void objectRemoved(Tile object) {
		super.objectRemoved(object);
		tileRemoved(object);
	}
	
	public void tileRemoved(Tile tile) {}
}
