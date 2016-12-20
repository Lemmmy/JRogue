package pw.lemmmy.jrogue.dungeon.generators;

import com.github.alexeyr.pcg.Pcg32;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.EntityChest;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public class RoomBasic extends Room {
	private static final float CHEST_PROBABILITY = 0.05f;

	private Pcg32 rand = new Pcg32();

	public RoomBasic(Level level, int roomX, int roomY, int roomWidth, int roomHeight) {
		super(level, roomX, roomY, roomWidth, roomHeight);
	}

	@Override
	public void build() {
		for (int y = getRoomY(); y < getRoomY() + getRoomHeight(); y++) {
			for (int x = getRoomX(); x < getRoomX() + getRoomWidth(); x++) {
				boolean wall = x == getRoomX() || x == getRoomX() + getRoomWidth() - 1 ||
							   y == getRoomY() || y == getRoomY() + getRoomHeight() - 1;

				if (wall) {
					if (x > getRoomX() && x < getRoomX() + getRoomWidth() - 1 && x % 4 == 0) {
						getLevel().setTileType(x, y, TileType.TILE_ROOM_TORCH_FIRE);
					} else {
						getLevel().setTileType(x, y, getWallType());
					}
				} else {
					getLevel().setTileType(x, y, getFloorType());
				}
			}
		}
	}

	@Override
	public void addFeatures() {
		if (rand.nextFloat() < CHEST_PROBABILITY) {
			int chestX = rand.nextInt(getRoomWidth() - 2) + getRoomX() + 1;
			int chestY = rand.nextInt(getRoomHeight() - 2) + getRoomY() + 1;

			EntityChest chest = new EntityChest(getLevel().getDungeon(), getLevel(), chestX, chestY);
			getLevel().addEntity(chest);
		}
	}

	protected TileType getWallType() {
		return TileType.TILE_ROOM_WALL;
	}

	protected TileType getFloorType() {
		return TileType.TILE_ROOM_FLOOR;
	}
}
