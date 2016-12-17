package pw.lemmmy.jrogue.dungeon.entities;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.*;

public class Container {
	private String name;

	private Map<Character, ItemStack> items = new LinkedHashMap<>();
	private List<ContainerListener> listeners = new ArrayList<>();

	public Container(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Character, ItemStack> getItems() {
		return items;
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
				ContainerEntry newEntry = new ContainerEntry(entry);
				listeners.forEach(l -> l.onItemIncrement(newEntry, stack.getCount()));
				return Optional.of(newEntry);
			}
		}

		char letter = getAvailableInventoryLetter();
		items.put(letter, stack);
		ContainerEntry newEntry = new ContainerEntry(letter, stack);
		listeners.forEach(l -> l.onItemAdd(newEntry));
		return Optional.of(newEntry);
	}

	public boolean canAdd(ItemStack stack) {
		for (ItemStack storedStack : items.values()) {
			if (stack.getItem().equals(storedStack.getItem())) {
				return true;
			}
		}

		return getAvailableInventoryLetter() != ' ';
	}

	public char getAvailableInventoryLetter() {
		for (char letter : Utils.INVENTORY_CHARS) {
			if (!items.containsKey(letter)) {
				return letter;
			}
		}

		return ' ';
	}

	public Optional<ContainerEntry> get(Character letter) {
		if (items.containsKey(letter)) {
			return Optional.of(new ContainerEntry(letter, items.get(letter)));
		} else {
			return Optional.empty();
		}
	}

	public boolean has(Character letter) {
		return items.containsKey(letter);
	}

	public void remove(Character letter) {
		if (items.containsKey(letter)) {
			ContainerEntry entry = new ContainerEntry(letter, items.get(letter));
			items.remove(letter);
			listeners.forEach(l -> l.onItemRemove(entry));
		}
	}

	public void addListener(ContainerListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ContainerListener listener) {
		listeners.remove(listener);
	}

	public void serialise(JSONObject obj) {
		items.entrySet().forEach(e -> {
			JSONObject serialisedItemStack = new JSONObject();
			e.getValue().serialise(serialisedItemStack);
			obj.put(e.getKey().toString(), serialisedItemStack);
		});
	}

	public interface ContainerListener {
		void onItemAdd(ContainerEntry entry);

		void onItemIncrement(ContainerEntry entry, int amount);

		void onItemRemove(ContainerEntry entry);
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
