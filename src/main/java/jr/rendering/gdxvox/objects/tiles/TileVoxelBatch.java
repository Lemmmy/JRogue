package jr.rendering.gdxvox.objects.tiles;

import jr.dungeon.tiles.Tile;
import jr.rendering.gdxvox.objects.VoxelBatch;
import jr.rendering.gdxvox.objects.VoxelModelInstance;

public class TileVoxelBatch extends VoxelBatch<Tile> {
	@Override
	protected void setAddedObjectPosition(Tile tile, VoxelModelInstance model) {
		model.setPosition(tile.getX(), 0, tile.getY());
	}
}
