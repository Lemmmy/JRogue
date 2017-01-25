package jr.dungeon.generators.rooms;

import jr.dungeon.Level;
import jr.dungeon.generators.GeneratorRooms;
import jr.dungeon.tiles.TileType;

public class RoomBasic extends Room {
	public RoomBasic(Level level, int roomX, int roomY, int roomWidth, int roomHeight) {
		super(level, roomX, roomY, roomWidth, roomHeight);
	}
	
	@Override
	public void build(GeneratorRooms generator) {
		for (int y = getY(); y < getY() + getHeight(); y++) {
			for (int x = getX(); x < getX() + getWidth(); x++) {
				boolean wall = x == getX() || x == getX() + getWidth() - 1 ||
					y == getY() || y == getY() + getHeight() - 1;
				
				if (wall) {
					if (x > getX() && x < getX() + getWidth() - 1 && x % 4 == 0) {
						getLevel().getTileStore().setTileType(x, y, getTorchTileType(generator));
					} else {
						getLevel().getTileStore().setTileType(x, y, getWallTileType(generator));
					}
				} else {
					getLevel().getTileStore().setTileType(x, y, getFloorTileType(generator));
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
