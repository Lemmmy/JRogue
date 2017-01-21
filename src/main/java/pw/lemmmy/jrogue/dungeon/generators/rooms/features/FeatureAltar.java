package pw.lemmmy.jrogue.dungeon.generators.rooms.features;

import com.github.alexeyr.pcg.Pcg32;
import pw.lemmmy.jrogue.dungeon.entities.decoration.EntityCandlestick;
import pw.lemmmy.jrogue.dungeon.entities.magic.EntityAltar;
import pw.lemmmy.jrogue.dungeon.generators.rooms.Room;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public class FeatureAltar extends SpecialRoomFeature {
	private static final double PROBABILITY_ALTAR_CANDLESTICK = 0.5;
	
	private Pcg32 rand = new Pcg32();
	
	@Override
	public void generate(Room room) {
		int altarX = rand.nextInt(room.getWidth() - 2) + room.getRoomX() + 1;
		int altarY = rand.nextInt(room.getHeight() - 2) + room.getRoomY() + 1;
		
		EntityAltar altar = new EntityAltar(room.getLevel().getDungeon(), room.getLevel(), altarX, altarY);
		room.getLevel().addEntity(altar);
		
		for (int y = altarY - 1; y < altarY + 2; y++) {
			for (int x = altarX - 1; x < altarX + 2; x++) {
				if (room.getLevel().getTileType(x, y).isFloorTile()) {
					room.getLevel().setTileType(x, y, TileType.TILE_ROOM_RUG);
				}
			}
		}
		
		if (rand.nextDouble() < PROBABILITY_ALTAR_CANDLESTICK) {
			int x = altarX - 1;
			int y = altarY;
			
			if (room.getLevel().getTileType(x, y).isFloorTile()) {
				EntityCandlestick cs1 = new EntityCandlestick(room.getLevel().getDungeon(), room.getLevel(), x, y);
				room.getLevel().addEntity(cs1);
			}
			
			x = altarX + 1;
			
			if (room.getLevel().getTileType(x, y).isFloorTile()) {
				EntityCandlestick cs2 = new EntityCandlestick(room.getLevel().getDungeon(), room.getLevel(), x, y);
				room.getLevel().addEntity(cs2);
			}
		}
	}
}
