package jr.dungeon.wishes;

import jr.dungeon.Dungeon;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;

import java.util.function.Consumer;

public class WishItem<T extends Item> implements Wish {
	private final Class<T> itemClass;
	private Consumer<T> itemConfigurer;

	public WishItem(Class<T> itemClass) {
		this(itemClass, null);
	}

	public WishItem(Class<T> itemClass, Consumer<T> itemConfigurer) {
		this.itemClass = itemClass;
		this.itemConfigurer = itemConfigurer;
	}

	@Override
	public void grant(Dungeon dungeon, Player player, String... args) {
		try {
			T item = itemClass.newInstance();

			if (player.getContainer().isPresent()) {
				if (itemConfigurer != null) {
					itemConfigurer.accept(item);
				}

				player.getContainer().get().add(new ItemStack(item));
			} else {
				dungeon.redYou("have no inventory!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
