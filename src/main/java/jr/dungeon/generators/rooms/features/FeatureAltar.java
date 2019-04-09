package jr.dungeon.generators.rooms.features;

import jr.dungeon.Level;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.entities.decoration.EntityCandlestick;
import jr.dungeon.entities.magic.EntityAltar;
import jr.dungeon.generators.rooms.Room;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.TileType;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Point;
import jr.utils.RandomUtils;

@Registered(id="specialRoomFeatureAltar")
public class FeatureAltar extends SpecialRoomFeature {
    private static final float PROBABILITY_ALTAR_CANDLESTICK = 0.5f;
    
    @Override
    public void generate(Room room) {
        Level level = room.level;
        
        Point altarPos = room.randomPoint();
        EntityAltar altar = new EntityAltar(level.getDungeon(), level, altarPos);
        level.entityStore.addEntity(altar);
        
        for (int y = altarPos.y - 1; y < altarPos.y + 2; y++) {
            for (int x = altarPos.x - 1; x < altarPos.x + 2; x++) {
                if (level.tileStore.getTileType(altarPos).isFloor()) {
                    level.tileStore.setTileType(altarPos, TileType.TILE_ROOM_RUG);
                }
            }
        }
        
        if (RandomUtils.randomFloat() < PROBABILITY_ALTAR_CANDLESTICK) {
            Point current = altarPos.add(-1, 0);
            if (level.tileStore.getTileType(current).isFloor())
                QuickSpawn.spawnClass(EntityCandlestick.class, level, current);
            
            current = altarPos.add(1, 0);
            if (level.tileStore.getTileType(current).isFloor())
                QuickSpawn.spawnClass(EntityCandlestick.class, level, current);
        }
    }
    
    @Override
    public Noun getName() {
        return Lexicon.holyAltar.clone();
    }
}
