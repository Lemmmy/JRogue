package jr.dungeon.generators.rooms;

import jr.dungeon.Level;
import jr.dungeon.generators.BuildingUtils;
import jr.dungeon.generators.GeneratorRooms;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.states.TileStateTorch;
import jr.utils.Colour;
import jr.utils.Point;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class RoomBasic extends Room {
	public RoomBasic(Level level, Point position, int roomWidth, int roomHeight) {
		super(level, position, roomWidth, roomHeight);
	}
	
	private void buildTorch(GeneratorRooms generator, Tile tile) {
		TileType torchType = getTorchTileType(generator);
		
		if (torchType != null) {
			tile.setType(torchType);
			
			if (tile.hasState() && tile.getState() instanceof TileStateTorch) {
				((TileStateTorch) tile.getState()).setColours(getTorchColours(generator));
			}
		}
	}
	
	@Override
	public void build(GeneratorRooms generator) {
		BuildingUtils.buildArea(tileStore, position, width, height, (t, p) -> {
			if (isEdgePoint(p)) { // should be a wall
				if (p.x > position.x && p.x < position.x + width - 1 && p.x % 4 == 0) {
					buildTorch(generator, t);
					return null;
				} else {
					return getWallTileType(generator);
				}
			} else {
				return getFloorTileType(generator);
			}
		});
	}
	
	@Override
	public void addFeatures() {}
	
	protected TileType getWallTileType(GeneratorRooms generator) {
		return generator == null ? TileType.TILE_ROOM_WALL : generator.getWallTileType();
	}
	
	protected TileType getFloorTileType(GeneratorRooms generator) {
		return generator == null ? TileType.TILE_ROOM_FLOOR : generator.getFloorTileType();
	}
	
	protected TileType getTorchTileType(GeneratorRooms generator) {
		return generator == null ? TileType.TILE_ROOM_TORCH : generator.getTorchTileType();
	}
	
	public Pair<Colour, Colour> getTorchColours(GeneratorRooms generator) {
		return generator == null
			 ? new ImmutablePair<>(new Colour(0xFF9B26FF), new Colour(0xFF1F0CFF))
			 : generator.getTorchColours();
	}
}
