package jr.rendering.gdxvox.objects.tiles;

import jr.dungeon.tiles.Tile;
import jr.rendering.gdxvox.objects.AbstractObjectRenderer;
import jr.rendering.gdxvox.objects.VoxelBatch;
import jr.rendering.gdxvox.objects.VoxelModelInstance;

public class TileVoxelBatch extends VoxelBatch<Tile> {
	public TileVoxelBatch(String rendererName) {
		super(rendererName);
	}
	
	public TileVoxelBatch(Class<? extends AbstractObjectRenderer> rendererClass) {
		super(rendererClass);
	}
	
	@Override
	protected void updateObjectPosition(Tile tile, VoxelModelInstance model) {
		model.setPosition(tile.getX(), 0, tile.getY());
	}
}
