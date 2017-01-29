package jr.dungeon.generators.rooms;

import jr.dungeon.Level;
import jr.dungeon.generators.GeneratorRooms;
import jr.dungeon.tiles.TileType;

public class RoomWater extends RoomBasic {
	public RoomWater(Level level, int roomX, int roomY, int roomWidth, int roomHeight) {
		super(level, roomX, roomY, roomWidth, roomHeight);
	}
	
	@Override
	protected TileType getFloorTileType(GeneratorRooms generator) {
		return TileType.TILE_ROOM_WATER;
	}
}
