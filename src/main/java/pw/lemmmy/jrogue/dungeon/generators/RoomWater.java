package pw.lemmmy.jrogue.dungeon.generators;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public class RoomWater extends RoomBasic {
	public RoomWater(Level level, int roomX, int roomY, int roomWidth, int roomHeight) {
		super(level, roomX, roomY, roomWidth, roomHeight);
	}

	@Override
	protected TileType getFloorType() {
		return TileType.TILE_ROOM_WATER;
	}
}
