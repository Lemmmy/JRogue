package pw.lemmmy.jrogue.dungeon.generators.rooms;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.generators.RoomGenerator;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public class RoomWater extends RoomBasic {
	public RoomWater(Level level, int roomX, int roomY, int roomWidth, int roomHeight) {
		super(level, roomX, roomY, roomWidth, roomHeight);
	}
	
	@Override
	protected TileType getFloorTileType(RoomGenerator generator) {
		return TileType.TILE_ROOM_WATER;
	}
}
