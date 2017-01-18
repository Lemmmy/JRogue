package pw.lemmmy.jrogue.dungeon.generators.rooms;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.generators.DungeonGenerator;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public class RoomBasic extends Room {
	public RoomBasic(Level level, int roomX, int roomY, int roomWidth, int roomHeight) {
		super(level, roomX, roomY, roomWidth, roomHeight);
	}
	
	@Override
	public void build(DungeonGenerator generator) {
		for (int y = getRoomY(); y < getRoomY() + getHeight(); y++) {
			for (int x = getRoomX(); x < getRoomX() + getWidth(); x++) {
				boolean wall = x == getRoomX() || x == getRoomX() + getWidth() - 1 ||
					y == getRoomY() || y == getRoomY() + getHeight() - 1;
				
				if (wall) {
					if (x > getRoomX() && x < getRoomX() + getWidth() - 1 && x % 4 == 0) {
						getLevel().setTileType(x, y, generator.getTorchTileType());
					} else {
						getLevel().setTileType(x, y, getWallType());
					}
				} else {
					getLevel().setTileType(x, y, getFloorType());
				}
			}
		}
	}
	
	@Override
	public void addFeatures() {}
	
	protected TileType getWallType() {
		return TileType.TILE_ROOM_WALL;
	}
	
	protected TileType getFloorType() {
		return TileType.TILE_ROOM_FLOOR;
	}
}
