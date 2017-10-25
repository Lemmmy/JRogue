package jr.rendering.gdxvox.objects.tiles.renderers;

import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdxvox.models.magicavoxel.ModelLoader;
import jr.rendering.gdxvox.models.magicavoxel.VoxelModel;
import jr.rendering.gdxvox.objects.VoxelModelInstance;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;
import jr.rendering.gdxvox.objects.tiles.TileVoxelBatch;

public class TileRendererDoor extends TileRenderer {
	private VoxelModel frameModel, doorModel;
	
	private DoorState doorState;
	
	public TileRendererDoor(DoorState doorState) {
		frameModel = ModelLoader.loadModel("models/tiles/door/door-frame.vox");
		doorModel = ModelLoader.loadModel("models/tiles/door/door.vox");
		
		this.doorState = doorState;
	}
	
	@Override
	public void tileAdded(Tile tile) {
		TileType[] adjacentTiles = tile.getLevel().tileStore.getAdjacentTileTypes(tile.getPosition());
		boolean h = adjacentTiles[0].isWallTile() || adjacentTiles[1].isWallTile();
		
		manager.getDynamicBatch()
			.add(tile, new VoxelModelInstance(frameModel)
			.setRotation(h ? 90 : 0));
		
		switch (doorState) {
			case OPEN:
				manager.getDynamicBatch()
					.add(tile, new VoxelModelInstance(doorModel)
					.setOffset((h ? -6 : -9) * (1 / 16f), 0, (h ? -9 : 6) * (1 / 16f))
					.setRotation(h ? 0 : 270));
				break;
			case CLOSED: // ha ha
				manager.getDynamicBatch()
					.add(tile, new VoxelModelInstance(doorModel)
					.setRotation(h ? 90 : 0));
				break;
			case BROKEN:
				break; // ha ha
		}
	}
	
	public enum DoorState {
		CLOSED, OPEN, BROKEN
	}
}
