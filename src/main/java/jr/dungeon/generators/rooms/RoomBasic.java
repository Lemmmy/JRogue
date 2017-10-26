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
	public static final int TORCH_SPACING = 5;
	
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
				}
			}
		}
		
		addTorches(generator);
	}
	
	protected void addTorches(GeneratorRooms generator) {
		int innerWidth = getWidth() - 1;
		int innerHeight = getHeight() - 1;
		int innerX = getX() + 1;
		int innerY = getY() + 1;
		
		int torchStartX = innerWidth % TORCH_SPACING + 1;
		int torchCountX = (int) Math.floor((float) innerWidth / (float) TORCH_SPACING);
		
		// top + bottom walls
		for (int i = 0; i < torchCountX; i++) {
			int tx = torchStartX + TORCH_SPACING * i;
			
			addTorch(generator, getX() + tx, innerY);
			addTorch(generator, getX() + tx, getY() + getHeight() - 2);
		}
		
		int torchStartY =  innerHeight % TORCH_SPACING + 1;
		int torchCountY = (int) Math.floor((float) innerHeight / (float) TORCH_SPACING);
		
		// left + right walls
		for (int i = 0; i < torchCountY; i++) {
			int ty = torchStartY + TORCH_SPACING * i;
			
			addTorch(generator, innerX, getY() + ty);
			addTorch(generator, getX() + getWidth() - 2, getY() + ty);
		}
	}
	
	protected void addTorch(GeneratorRooms generator, int x, int y) {
		getLevel().tileStore.setTileType(x, y, TileType.TILE_DEBUG_B);
		
		if (getLevel().entityStore.getAdjacentQueuedEntities(x, y).stream()
			.anyMatch(EntityTorch.class::isInstance)) return;
		
		getLevel().tileStore.setTileType(x, y, TileType.TILE_DEBUG_A);
		
		EntityTorch torch = QuickSpawn.spawnClass(EntityTorch.class, getLevel(), x, y);
		if (torch != null) torch.setColours(getTorchColours(generator));
	}
	
	@Override
	public void addFeatures() {}
	
	protected TileType getWallTileType(GeneratorRooms generator) {
		return generator == null ? TileType.TILE_ROOM_WALL : generator.getWallTileType();
	}
	
	protected TileType getFloorTileType(GeneratorRooms generator) {
		return generator == null ? TileType.TILE_ROOM_FLOOR : generator.getFloorTileType();
	}
	
	public Pair<Colour, Colour> getTorchColours(GeneratorRooms generator) {
		if (generator == null) {
			return new ImmutablePair<>(new Colour(0xFF9B26FF), new Colour(0xFF1F0CFF));
		}
		
		return generator.getTorchColours();
	}
}
