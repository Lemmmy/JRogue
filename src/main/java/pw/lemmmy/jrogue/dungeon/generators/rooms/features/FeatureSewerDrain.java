package pw.lemmmy.jrogue.dungeon.generators.rooms.features;

import com.github.alexeyr.pcg.Pcg32;
import pw.lemmmy.jrogue.dungeon.generators.rooms.Room;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public class FeatureSewerDrain extends SpecialRoomFeature {
	private Pcg32 rand = new Pcg32();
	
	@Override
	public void generate(Room room) {
		int drainX = rand.nextInt(room.getWidth() - 2) + room.getRoomX() + 1;
		int drainY = room.getRoomY();
		
		if (
			room.getLevel().getTileType(drainX, drainY).isWallTile() &&
			!room.getLevel().getTileType(drainX, drainY).isDoor()
		) {
			TileType drainTile = rand.nextBoolean() ? TileType.TILE_SEWER_DRAIN :
								 					  TileType.TILE_SEWER_DRAIN_EMPTY;
			
			room.getLevel().setTileType(drainX, drainY, drainTile);
			
			if (
				!room.getLevel().getTileType(drainX, drainY + 1).isWater() &&
				room.getLevel().getTileType(drainX, drainY + 1).isFloorTile()
			) {
				room.getLevel().setTileType(drainX, drainY + 1, TileType.TILE_SEWER_WATER);
			}
		}
	}
}
