package jr.dungeon.generators.rooms.features;

import jr.dungeon.generators.rooms.Room;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.language.Noun;
import jr.utils.Point;
import jr.utils.RandomUtils;

@Registered(id="specialRoomFeatureSewerDrain")
public class FeatureSewerDrain extends SpecialRoomFeature {
	@Override
	public void generate(Room room) {
		Point drainPosition = room.randomPoint().setY(room.position.y);
		Tile drainTile = room.level.tileStore.getTile(drainPosition);
		
		if (drainTile.getType().isWallTile() && !drainTile.getType().isDoor()) {
			drainTile.setType(RandomUtils.rollD2() ? TileType.TILE_SEWER_DRAIN : TileType.TILE_SEWER_DRAIN_EMPTY);
			
			Tile underTile = room.level.tileStore.getTile(drainPosition.add(0, 1));
			
			if (!underTile.getType().isWater() && underTile.getType().isFloor()) {
				underTile.setType(TileType.TILE_SEWER_WATER);
			}
		}
	}
	
	@Override
	public Noun getName() {
		return null;
	}
}
