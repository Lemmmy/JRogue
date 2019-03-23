package jr.dungeon.generators.rooms.features;

import com.github.alexeyr.pcg.Pcg32;
import jr.dungeon.Level;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.entities.decoration.EntityCandlestick;
import jr.dungeon.entities.magic.EntityAltar;
import jr.dungeon.generators.rooms.Room;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.TileType;
import jr.language.Lexicon;
import jr.language.Noun;

@Registered(id="specialRoomFeatureAltar")
public class FeatureAltar extends SpecialRoomFeature {
	private static final double PROBABILITY_ALTAR_CANDLESTICK = 0.5;
	
	private Pcg32 rand = new Pcg32();
	
	@Override
	public void generate(Room room) {
		Level level = room.getLevel();
		int altarX = rand.nextInt(room.getWidth() - 2) + room.getX() + 1;
		int altarY = rand.nextInt(room.getHeight() - 2) + room.getY() + 1;
		
		EntityAltar altar = new EntityAltar(level.getDungeon(), level, altarX, altarY);
		level.entityStore.addEntity(altar);
		
		for (int y = altarY - 1; y < altarY + 2; y++) {
			for (int x = altarX - 1; x < altarX + 2; x++) {
				if (level.tileStore.getTileType(x, y).isFloor()) {
					level.tileStore.setTileType(x, y, TileType.TILE_ROOM_RUG);
				}
			}
		}
		
		if (rand.nextDouble() < PROBABILITY_ALTAR_CANDLESTICK) {
			int currentX = altarX - 1;
			
			if (level.tileStore.getTileType(currentX, altarY).isFloor())
				QuickSpawn.spawnClass(EntityCandlestick.class, level, currentX, altarY);
			
			currentX = altarX + 1;
			
			if (level.tileStore.getTileType(currentX, altarY).isFloor())
				QuickSpawn.spawnClass(EntityCandlestick.class, level, currentX, altarY);
		}
	}
	
	@Override
	public Noun getName() {
		return Lexicon.holyAltar.clone();
	}
}
