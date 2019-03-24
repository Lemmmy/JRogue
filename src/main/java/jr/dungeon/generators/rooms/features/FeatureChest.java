package jr.dungeon.generators.rooms.features;

import com.github.alexeyr.pcg.Pcg32;
import jr.JRogue;
import jr.dungeon.Level;
import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.containers.EntityChest;
import jr.dungeon.generators.rooms.Room;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.SpecialChestSpawn;
import jr.dungeon.items.comestibles.*;
import jr.dungeon.items.magical.ItemSpellbook;
import jr.dungeon.items.valuables.ItemGem;
import jr.dungeon.items.valuables.ItemThermometer;
import jr.dungeon.items.weapons.ItemDagger;
import jr.dungeon.items.weapons.ItemLongsword;
import jr.dungeon.items.weapons.ItemShortsword;
import jr.dungeon.serialisation.Registered;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.RandomUtils;
import jr.utils.WeightedCollection;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Registered(id="specialRoomFeatureChest")
public class FeatureChest extends SpecialRoomFeature {
	private static final WeightedCollection<ItemGroup> ITEM_GROUPS = new WeightedCollection<>();
	
	static {
		// FOOD
		ITEM_GROUPS.add(30, new ItemGroup(
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
		ITEM_GROUPS.add(6, new ItemGroup(
			ItemDagger.class,
			ItemShortsword.class,
			ItemLongsword.class
		));
		
		// GEMS
		ITEM_GROUPS.add(4, new ItemGroup(
			ItemGem.class
		));
		
		// MISC
		ITEM_GROUPS.add(1, new ItemGroup(
			ItemThermometer.class,
			ItemSpellbook.class
		));
	}
	
	private static final Pcg32 RAND = new Pcg32();
	
	@Override
	public void generate(Room room) {
		int chestX = RAND.nextInt(room.getWidth() - 2) + room.getX() + 1;
		int chestY = RAND.nextInt(room.getHeight() - 2) + room.getY() + 1;
		
		EntityChest chest = new EntityChest(room.getLevel().getDungeon(), room.getLevel(), chestX, chestY);
		populateChest(room, chest);
		room.getLevel().entityStore.addEntity(chest);
	}
	
	@Override
	public Noun getName() {
		return Lexicon.chest.clone();
	}
	
	private void populateChest(Room room, EntityChest chest) {
		if (!chest.getContainer().isPresent()) {
			return;
		}
		
		Container container = chest.getContainer().get();
		
		int itemAmount = RandomUtils.roll(4) - 1; // possibility that chests can be empty
		
		for (int i = 0; i < itemAmount; i++) {
			ItemGroup group = ITEM_GROUPS.next();
			Class<? extends Item> itemClass = group.getRandomItem();
			
			populateChestItem(room, chest, container, itemClass);
		}
	}
	
	private void populateChestItem(Room room, EntityChest chest, Container container, Class<? extends Item> itemClass) {
		Constructor<? extends Item> constructor = ConstructorUtils.getAccessibleConstructor(itemClass, Level.class);
		Item item;
		
		if (constructor != null) {
			try {
				item = constructor.newInstance(room.getLevel());
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				JRogue.getLogger().error("Error adding chest items", e);
				return;
			}
		} else {
			constructor = ConstructorUtils.getAccessibleConstructor(itemClass);
			
			try {
				item = constructor.newInstance();
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				JRogue.getLogger().error("Error adding chest items", e);
				return;
			}
		}
		
		if (item instanceof SpecialChestSpawn) {
			((SpecialChestSpawn) item).onSpawnInChest(chest, container);
		} else {
			ItemStack stack = new ItemStack(item, 1);
			container.add(stack);
		}
	}
		
	protected static class ItemGroup {
		private List<Class<? extends Item>> items = new ArrayList<>();
		
		@SafeVarargs
		private ItemGroup(Class<? extends Item>... items) {
			this.items.addAll(Arrays.asList(items));
		}
		
		public Class<? extends Item> getRandomItem() {
			return RandomUtils.randomFrom(items);
		}
	}
}
