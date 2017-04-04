package jr.dungeon.generators.rooms.features;

import com.github.alexeyr.pcg.Pcg32;
import jr.dungeon.entities.decoration.EntityCandlestick;
import jr.dungeon.entities.magic.EntityAltar;
import jr.dungeon.generators.rooms.Room;
import jr.dungeon.tiles.TileType;

public class FeatureAltar extends SpecialRoomFeature {
	private static final double PROBABILITY_ALTAR_CANDLESTICK = 0.5;
	
	private Pcg32 rand = new Pcg32();
	
	@Override
	public void generate(Room room) {
		int altarX = rand.nextInt(room.getWidth() - 2) + room.getX() + 1;
		int altarY = rand.nextInt(room.getHeight() - 2) + room.getY() + 1;
		
		EntityAltar altar = new EntityAltar(room.getLevel().getDungeon(), room.getLevel(), altarX, altarY);
		room.getLevel().entityStore.addEntity(altar);
		
		for (int y = altarY - 1; y < altarY + 2; y++) {
			for (int x = altarX - 1; x < altarX + 2; x++) {
				if (room.getLevel().tileStore.getTileType(x, y).isFloor()) {
					room.getLevel().tileStore.setTileType(x, y, TileType.TILE_ROOM_RUG);
				}
			}
		}
		
		if (rand.nextDouble() < PROBABILITY_ALTAR_CANDLESTICK) {
			int currentX = altarX - 1;
			
			if (room.getLevel().tileStore.getTileType(currentX, altarY).isFloor()) {
				EntityCandlestick cs1 = new EntityCandlestick(room.getLevel().getDungeon(), room.getLevel(), currentX, altarY);
				room.getLevel().entityStore.addEntity(cs1);
			}
			
			currentX = altarX + 1;
			
			if (room.getLevel().tileStore.getTileType(currentX, altarY).isFloor()) {
				EntityCandlestick cs2 = new EntityCandlestick(room.getLevel().getDungeon(), room.getLevel(), currentX, altarY);
				room.getLevel().entityStore.addEntity(cs2);
			}
		}
	}
	
	@Override
	public String getName(boolean plural) {
		return plural ? "altars" : "altar";
	}
}
