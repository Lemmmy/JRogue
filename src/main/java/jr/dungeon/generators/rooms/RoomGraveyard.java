package jr.dungeon.generators.rooms;

import com.github.alexeyr.pcg.Pcg32;
import jr.dungeon.Level;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.entities.decoration.EntityGravestone;
import jr.dungeon.entities.monsters.zombies.MonsterGoblinZombie;
import jr.dungeon.entities.monsters.zombies.MonsterZombie;
import jr.dungeon.generators.GeneratorRooms;
import jr.dungeon.tiles.TileType;
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
	
	private static final Pcg32 RAND = new Pcg32();
	
	public RoomGraveyard(Level level, int roomX, int roomY, int roomWidth, int roomHeight) {
		super(level, roomX, roomY, roomWidth, roomHeight);
	}
	
	@Override
	public void addFeatures() {
		super.addFeatures();
		
		int graveCount = RandomUtils.random(
			MIN_GRAVES,
			Math.max(MIN_MAX_GRAVES, (getWidth() - 2) * (getHeight() - 2) / 10)
		);
		
		for (int i = 0; i < graveCount; i++) {
			addGravestone();
		}
	}
	
	private void addGravestone() {
		int x = RAND.nextInt(getWidth() - 2) + getX() + 1;
		int y = RAND.nextInt(getHeight() - 2) + getY() + 1;
		
		if (
			getLevel().tileStore.getTileType(x, y).isFloor() &&
			getLevel().entityStore.getEntitiesAt(x, y).size() == 0
		) {
			getLevel().entityStore.addEntity(new EntityGravestone(getLevel().getDungeon(), getLevel(), x, y));
			
			if (RandomUtils.rollD2()) {
				int zombieCount = RandomUtils.random(MIN_ZOMBIES, MAX_ZOMBIES);
				
				for (int i = 0; i < zombieCount; i++) {
					QuickSpawn.spawnClass(RandomUtils.randomFrom(ZOMBIE_CLASSES), getLevel(), x, y);
				}
			}
		}
	}
	
	@Override
	protected TileType getFloorTileType(GeneratorRooms generator) {
		return TileType.TILE_ROOM_DIRT;
	}
}
