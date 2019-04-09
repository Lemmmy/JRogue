package jr.dungeon.generators.rooms;

import jr.dungeon.Level;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.entities.decoration.EntityGravestone;
import jr.dungeon.entities.monsters.zombies.MonsterGoblinZombie;
import jr.dungeon.entities.monsters.zombies.MonsterZombie;
import jr.dungeon.generators.GeneratorRooms;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;
import jr.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class RoomGraveyard extends RoomBasic {
    private static final int MIN_GRAVES = 2;
    private static final int MIN_MAX_GRAVES = 5;
    
    private static final int MIN_ZOMBIES = 1;
    private static final int MAX_ZOMBIES = 3;
    
    private static final List<Class<? extends MonsterZombie>> ZOMBIE_CLASSES = new ArrayList<>();
    
    static {
        ZOMBIE_CLASSES.add(MonsterGoblinZombie.class);
    }
    
    public RoomGraveyard(Level level, Point position, int roomWidth, int roomHeight) {
        super(level, position, roomWidth, roomHeight);
    }
    
    @Override
    public void addFeatures() {
        super.addFeatures();
        
        int graveCount = RandomUtils.random(
            MIN_GRAVES,
            Math.max(MIN_MAX_GRAVES, (width - 2) * (height - 2) / 10)
        );
        
        for (int i = 0; i < graveCount; i++) {
            addGravestone();
        }
    }
    
    private void addGravestone() {
        Point point = randomPoint();
        
        if (
            tileStore.getTileType(point).isFloor() &&
            !level.entityStore.areEntitiesAt(point)
        ) {
            level.entityStore.addEntity(new EntityGravestone(level.getDungeon(), level, point));
            
            if (RandomUtils.rollD2()) {
                int zombieCount = RandomUtils.random(MIN_ZOMBIES, MAX_ZOMBIES);
                
                for (int i = 0; i < zombieCount; i++) {
                    QuickSpawn.spawnClass(RandomUtils.randomFrom(ZOMBIE_CLASSES), level, point);
                }
            }
        }
    }
    
    @Override
    protected TileType getFloorTileType(GeneratorRooms generator) {
        return TileType.TILE_ROOM_DIRT;
    }
}
