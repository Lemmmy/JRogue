package jr.rendering.gdxvox.objects.tiles;

import jr.dungeon.tiles.TileType;
import jr.rendering.gdxvox.objects.tiles.renderers.TileRendererDoor;
import jr.rendering.gdxvox.objects.tiles.renderers.TileRendererFloor;
import jr.rendering.gdxvox.objects.tiles.renderers.TileRendererWall;

public class TileRenderers {
	@SuppressWarnings("unchecked")
	@TileRendererList
	public static void addRenderers(TileRendererMap m) {
		m.addRenderers(new TileRendererWall(), TileType.TILE_ROOM_WALL);
		m.addRenderers(new TileRendererFloor(), TileType.TILE_ROOM_FLOOR);
		m.addRenderers(new TileRendererDoor(TileRendererDoor.DoorState.CLOSED), TileType.TILE_ROOM_DOOR_CLOSED);
		m.addRenderers(new TileRendererDoor(TileRendererDoor.DoorState.CLOSED), TileType.TILE_ROOM_DOOR_LOCKED);
		m.addRenderers(new TileRendererDoor(TileRendererDoor.DoorState.OPEN), TileType.TILE_ROOM_DOOR_OPEN);
		m.addRenderers(new TileRendererDoor(TileRendererDoor.DoorState.BROKEN), TileType.TILE_ROOM_DOOR_BROKEN);
	}
}
