package jr.dungeon.generators.rooms;

import jr.dungeon.Level;
import jr.dungeon.generators.GeneratorRooms;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;

public class RoomIce extends RoomBasic {
	public RoomIce(Level level, Point position, int roomWidth, int roomHeight) {
		super(level, position, roomWidth, roomHeight);
	}
	
	@Override
	protected TileType getFloorTileType(GeneratorRooms generator) {
		return TileType.TILE_ROOM_ICE;
	}
}
