package pw.lemmmy.jrogue.dungeon.generators.rooms;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.generators.GeneratorRooms;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public class RoomBasic extends Room {
	public RoomBasic(Level level, int roomX, int roomY, int roomWidth, int roomHeight) {
		super(level, roomX, roomY, roomWidth, roomHeight);
	}
	
	@Override
	public void build(GeneratorRooms generator) {
		for (int y = getRoomY(); y < getRoomY() + getHeight(); y++) {
			for (int x = getRoomX(); x < getRoomX() + getWidth(); x++) {
				boolean wall = x == getRoomX() || x == getRoomX() + getWidth() - 1 ||
					y == getRoomY() || y == getRoomY() + getHeight() - 1;
				
				if (wall) {
					if (x > getRoomX() && x < getRoomX() + getWidth() - 1 && x % 4 == 0) {
						getLevel().setTileType(x, y, getTorchTileType(generator));
					} else {
						getLevel().setTileType(x, y, getWallTileType(generator));
					}
				} else {
					getLevel().setTileType(x, y, getFloorTileType(generator));
				}
			}
		}
	}
	
	@Override
	public void addFeatures() {}
	
	protected TileType getWallTileType(GeneratorRooms generator) {
		if (generator == null) {
			return TileType.TILE_ROOM_WALL;
		}
		
		return generator.getWallTileType();
	}
	
	protected TileType getFloorTileType(GeneratorRooms generator) {
		if (generator == null) {
			return TileType.TILE_ROOM_FLOOR;
		}
		
		return generator.getFloorTileType();
	}
	
	protected TileType getTorchTileType(GeneratorRooms generator) {
		if (generator == null) {
			return TileType.TILE_ROOM_TORCH_FIRE;
		}
		
		if (generator.getTorchTileType() == null) {
			return getWallTileType(generator);
		} else {
			return generator.getTorchTileType();
		}
	}
}
