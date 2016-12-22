package pw.lemmmy.jrogue.dungeon.generators;

import com.github.alexeyr.pcg.Pcg32;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.Container;
import pw.lemmmy.jrogue.dungeon.entities.EntityChest;
import pw.lemmmy.jrogue.dungeon.items.*;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.Utils;
import pw.lemmmy.jrogue.utils.WeightedCollection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoomBasic extends Room {
	private static final float CHEST_PROBABILITY = 0.1f;

	private static final WeightedCollection<ItemGroup> ITEM_GROUPS = new WeightedCollection<>();

	static {
		// FOOD
		ITEM_GROUPS.add(15, new ItemGroup(
			ItemApple.class,
			ItemBanana.class,
			ItemBread.class,
			ItemCarrot.class,
			ItemCherries.class,
			ItemCorn.class,
			ItemLemon.class,
			ItemOrange.class
		));

		// WEAPONS
		ITEM_GROUPS.add(3, new ItemGroup(
			ItemDagger.class,
			ItemShortsword.class,
			ItemLongsword.class
		));

		// GEMS
		ITEM_GROUPS.add(2, new ItemGroup(
			ItemGem.class
		));
	}

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
			addRandomChest();
		}
	}

	private void addRandomChest() {
		int chestX = rand.nextInt(getRoomWidth() - 2) + getRoomX() + 1;
		int chestY = rand.nextInt(getRoomHeight() - 2) + getRoomY() + 1;

		EntityChest chest = new EntityChest(getLevel().getDungeon(), getLevel(), chestX, chestY);
		populateChest(chest);
		getLevel().addEntity(chest);
	}

	private void populateChest(EntityChest chest) {
		if (!chest.getContainer().isPresent()) {
			return;
		}

		Container container = chest.getContainer().get();

		for (int i = 0; i < 3; i++) {
			ItemGroup group = ITEM_GROUPS.next();
			Class itemClass = group.getRandomItem();

			populateChestItem(container, itemClass);
		}
	}

	@SuppressWarnings("unchecked")
	private void populateChestItem(Container container, Class itemClass) {
		Constructor constructor = ConstructorUtils.getAccessibleConstructor(itemClass, Level.class);
		Item item;

		if (constructor != null) {
			try {
				item = (Item) constructor.newInstance(getLevel());
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
				return;
			}
		} else {
			constructor = ConstructorUtils.getAccessibleConstructor(itemClass);

			try {
				item = (Item) constructor.newInstance();
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
				return;
			}
		}

		ItemStack stack = new ItemStack(item, 1);
		container.add(stack);
	}

	protected TileType getWallType() {
		return TileType.TILE_ROOM_WALL;
	}

	protected TileType getFloorType() {
		return TileType.TILE_ROOM_FLOOR;
	}

	protected static class ItemGroup {
		private List<Class> items = new ArrayList<>();

		private ItemGroup(Class... items) {
			this.items.addAll(Arrays.asList(items));
		}

		public Class getRandomItem() {
			return Utils.randomFrom(items);
		}
	}
}
