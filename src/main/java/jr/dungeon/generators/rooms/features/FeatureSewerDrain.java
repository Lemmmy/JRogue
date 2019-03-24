package jr.dungeon.generators.rooms.features;

import com.github.alexeyr.pcg.Pcg32;
import jr.dungeon.generators.rooms.Room;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.TileType;
import jr.language.Noun;

@Registered(id="specialRoomFeatureSewerDrain")
public class FeatureSewerDrain extends SpecialRoomFeature {
	private static final Pcg32 RAND = new Pcg32();
	
	@Override
	public void generate(Room room) {
		int drainX = RAND.nextInt(room.getWidth() - 2) + room.getX() + 1;
		int drainY = room.getY();
		
		if (
			room.getLevel().tileStore.getTileType(drainX, drainY).isWallTile() &&
			!room.getLevel().tileStore.getTileType(drainX, drainY).isDoor()
		) {
			TileType drainTile = RAND.nextBoolean() ? TileType.TILE_SEWER_DRAIN :
								 TileType.TILE_SEWER_DRAIN_EMPTY;
			
			room.getLevel().tileStore.setTileType(drainX, drainY, drainTile);
			
			if (
				!room.getLevel().tileStore.getTileType(drainX, drainY + 1).isWater() &&
				room.getLevel().tileStore.getTileType(drainX, drainY + 1).isFloor()
			) {
				room.getLevel().tileStore.setTileType(drainX, drainY + 1, TileType.TILE_SEWER_WATER);
			}
		}
	}
	
	@Override
	public Noun getName() {
		return null;
	}
}
