package jr.dungeon.generators.rooms;

import jr.dungeon.Level;
import jr.dungeon.TileStore;
import jr.dungeon.generators.GeneratorRooms;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.states.TileStateTorch;
import jr.utils.Colour;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class RoomBasic extends Room {
	public RoomBasic(Level level, int roomX, int roomY, int roomWidth, int roomHeight) {
		super(level, roomX, roomY, roomWidth, roomHeight);
	}
	
	@Override
	public void build(GeneratorRooms generator) {
		TileStore ts = getLevel().tileStore;
		
		for (int y = getY(); y < getY() + getHeight(); y++) {
			for (int x = getX(); x < getX() + getWidth(); x++) {
				boolean wall = x == getX() || x == getX() + getWidth() - 1 ||
					y == getY() || y == getY() + getHeight() - 1;
				
				if (wall) {
					if (x > getX() && x < getX() + getWidth() - 1 && x % 4 == 0) {
						ts.setTileType(x, y, getTorchTileType(generator));
						
						if (getTorchTileType(generator) != null
							&& ts.getTile(x, y).hasState()
							&& ts.getTile(x, y).getState() instanceof TileStateTorch) {
							((TileStateTorch) ts.getTile(x, y).getState()).setColours(getTorchColours(generator));
						}
					} else {
						ts.setTileType(x, y, getWallTileType(generator));
					}
				} else {
					ts.setTileType(x, y, getFloorTileType(generator));
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
			return TileType.TILE_ROOM_TORCH;
		}
		
		return generator.getTorchTileType();
	}
	
	public Pair<Colour, Colour> getTorchColours(GeneratorRooms generator) {
		if (generator == null) {
			return new ImmutablePair<>(new Colour(0xFF9B26FF), new Colour(0xFF1F0CFF));
		}
		
		return generator.getTorchColours();
	}
}
