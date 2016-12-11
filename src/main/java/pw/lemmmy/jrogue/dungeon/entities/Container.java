package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class Container {
	private Map<Character, ItemStack> items = new LinkedHashMap<>();

	public char getAvailableInventoryLetter() {
		for (char letter : Utils.INVENTORY_CHARS) {
			if (!items.containsKey(letter)) {
				return letter;
			}
		}

		return ' ';
	}

	public Optional<ContainerEntry> add(ItemStack stack) {
		Item item = stack.getItem();

		if (!canAdd(stack)) {
			return Optional.empty();
		}

		for (Map.Entry<Character, ItemStack> entry : items.entrySet()) {
			ItemStack storedStack = entry.getValue();

			if (item.equals(storedStack.getItem())) {
				storedStack.addCount(stack.getCount());

				return Optional.of(new ContainerEntry(entry));
			}
		}

		char letter = getAvailableInventoryLetter();
		items.put(letter, stack);

		return Optional.of(new ContainerEntry(letter, stack));
	}

	public boolean canAdd(ItemStack stack) {
		for (ItemStack storedStack : items.values()) {
			if (stack.getItem().equals(storedStack.getItem())) {
				return true;
			}
		}

		return getAvailableInventoryLetter() != ' ';
	}

	public Optional<ContainerEntry> get(Character letter) {
		if (items.containsKey(letter)) {
			return Optional.of(new ContainerEntry(letter, items.get(letter)));
		} else {
			return Optional.empty();
		}
	}

	public class ContainerEntry {
		private final Character letter;
		private ItemStack stack;

		public ContainerEntry(Map.Entry<Character, ItemStack> entry) {
			this.letter = entry.getKey();
			this.stack = entry.getValue();
		}

		public ContainerEntry(final Character letter, final ItemStack stack) {
			this.letter = letter;
			this.stack = stack;
		}

		public Character getLetter() {
			return letter;
		}

		public ItemStack getStack() {
			return stack;
		}

		public ItemStack setValue(final ItemStack value) {
			final ItemStack oldValue = this.stack;
			this.stack = value;
			items.put(letter, value);
			return oldValue;
		}
	}
}
