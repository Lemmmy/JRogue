package jr.dungeon.generators.rooms;

import jr.dungeon.Level;
import jr.dungeon.TileStore;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.entities.decoration.EntityTorch;
import jr.dungeon.generators.GeneratorRooms;
import jr.dungeon.tiles.TileType;
import jr.utils.Colour;
import jr.utils.RandomUtils;
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
					ts.setTileType(x, y, getWallTileType(generator));
				} else {
					ts.setTileType(x, y, getFloorTileType(generator));
					
					if (RandomUtils.randomFloat() < 0.1) {
						EntityTorch torch = QuickSpawn.spawnClass(EntityTorch.class, getLevel(), x, y);
						if (torch != null) torch.setColours(getTorchColours(generator));
					}
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
	
	public Pair<Colour, Colour> getTorchColours(GeneratorRooms generator) {
		if (generator == null) {
			return new ImmutablePair<>(new Colour(0xFF9B26FF), new Colour(0xFF1F0CFF));
		}
		
		return generator.getTorchColours();
	}
}
