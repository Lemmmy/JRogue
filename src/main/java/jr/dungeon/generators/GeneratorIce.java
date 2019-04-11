package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.generators.rooms.RoomBasic;
import jr.dungeon.generators.rooms.RoomIce;
import jr.dungeon.generators.rooms.Rooms;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Tile;
import jr.utils.Colour;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

@Registered(id="generatorIce")
public class GeneratorIce extends GeneratorRooms {
    static {
        Rooms.registerRoom(GeneratorIce.class, RoomBasic.class, 15);
        Rooms.registerRoom(GeneratorIce.class, RoomIce.class, 1);
    }
    
    public GeneratorIce(Level level, Tile sourceTile) {
        super(level, sourceTile);
    }
    
    @Override
    public Climate getClimate() {
        return Climate.COLD;
    }
    
    @Override
    public Class<? extends DungeonGenerator> getNextGenerator() {
        return GeneratorIce.class;
    }
    
    @Override
    public MonsterSpawningStrategy getMonsterSpawningStrategy() {
        return MonsterSpawningStrategy.ICE;
    }
    
    @Override
    public boolean generate() {
        level.setName("Icy Dungeon");
        
        return super.generate() && verify();
    }
    
    @Override
    public Pair<Colour, Colour> getTorchColours() {
        return new ImmutablePair<>(new Colour(0x8BD1ECFF), new Colour(0x0CE6FFFF));
    }
}
