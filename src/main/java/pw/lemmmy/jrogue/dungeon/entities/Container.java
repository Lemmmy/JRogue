package pw.lemmmy.jrogue.dungeon.entities;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Serialisable;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.*;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Container implements Serialisable {
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
	
	public int getItemCount() {
		return items.size();
	}
	
	public boolean isEmpty() {
		return getItemCount() == 0;
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
	
	public void transfer(Container destContainer, Character character, int amount, Player player) {
		ItemStack itemStack = items.get(character);
		
		if (!destContainer.canAdd(itemStack)) {
			return;
		}
		
		if (player != null) {
			if (player.getLeftHand().getStack() == itemStack) {
				player.setLeftHand(null);
			}
			
			if (player.getRightHand().getStack() == itemStack) {
				player.setRightHand(null);
			}
		}
		
		destContainer.add(itemStack);
		items.remove(character);
	}
	
	public void addListener(ContainerListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ContainerListener listener) {
		listeners.remove(listener);
	}
	
	public static Container createFromJSON(JSONObject obj) {
		Container container = new Container(obj.getString("name"));
		container.unserialise(obj);
		return container;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		obj.put("name", name);
		
		JSONObject serialisedItems = new JSONObject();
		items.entrySet().forEach(e -> {
			JSONObject serialisedItemStack = new JSONObject();
			e.getValue().serialise(serialisedItemStack);
			serialisedItems.put(e.getKey().toString(), serialisedItemStack);
		});
		obj.put("items", serialisedItems);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		JSONObject serialisedItems = obj.getJSONObject("items");
		serialisedItems.keySet().forEach(k -> {
			JSONObject v = serialisedItems.getJSONObject(k);
			Character letter = k.charAt(0);
			
			Optional<ItemStack> itemStackOptional = ItemStack.createFromJSON(v);
			itemStackOptional.ifPresent(itemStack -> items.put(letter, itemStack));
		});
	}
	
	public Map<Character, ItemStack> getWieldables() {
		return items.entrySet().stream()
			.filter(e -> e.getValue().getItem() instanceof Wieldable)
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	public Map<Character, ItemStack> getComestibles() {
		return items.entrySet().stream()
			.filter(e -> e.getValue().getItem() instanceof ItemComestible)
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	public Map<Character, ItemStack> getDrinkables() {
		return items.entrySet().stream()
			.filter(e -> e.getValue().getItem() instanceof ItemDrinkable)
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
