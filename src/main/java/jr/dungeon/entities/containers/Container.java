package jr.dungeon.entities.containers;

import com.google.gson.annotations.Expose;
import jr.ErrorHandler;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.EventListener;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.Wieldable;
import jr.dungeon.items.comestibles.ItemComestible;
import jr.dungeon.items.quaffable.ItemQuaffable;
import jr.language.Noun;
import jr.language.transformers.Capitalise;
import jr.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class Container implements EventListener {
	@Expose	@Getter @Setter	private String name;
	
	@Expose @Getter private Map<Character, ItemStack> items = new LinkedHashMap<>();
	private List<ContainerListener> listeners = new ArrayList<>();
	
	public Container(Noun name) {
		this.name = name.build(Capitalise.first);
	}
	
	public Container(String name) {
		this.name = name;
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
		
		if (stack.getItem().shouldStack()) {
			for (Map.Entry<Character, ItemStack> entry : items.entrySet()) {
				ItemStack storedStack = entry.getValue();
				
				if (item.equals(storedStack.getItem())) {
					storedStack.addCount(stack.getCount());
					ContainerEntry newEntry = new ContainerEntry(entry);
					listeners.forEach(l -> l.onItemIncrement(newEntry, stack.getCount()));
					return Optional.of(newEntry);
				}
			}
		}
		
		char letter = getAvailableInventoryLetter();
		items.put(letter, stack);
		ContainerEntry newEntry = new ContainerEntry(letter, stack);
		listeners.forEach(l -> l.onItemAdd(newEntry));
		return Optional.of(newEntry);
	}
	
	public boolean canAdd(ItemStack stack) {
		if (stack.getItem().shouldStack()) {
			for (ItemStack storedStack : items.values()) {
				if (stack.getItem().equals(storedStack.getItem())) {
					return true;
				}
			}
		}
		
		return getAvailableInventoryLetter() != 0;
	}
	
	public char getAvailableInventoryLetter() {
		for (char letter : Utils.INVENTORY_CHARS) {
			if (!items.containsKey(letter)) {
				return letter;
			}
		}
		
		return 0;
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
			if (player.getLeftHand() != null) {
				if (player.getLeftHand().getStack() == itemStack) {
					player.setLeftHand(null);
				}
			}
			
			if (player.getRightHand() != null) {
				if (player.getRightHand().getStack() == itemStack) {
					player.setRightHand(null);
				}
			}
		}
		
		destContainer.add(itemStack);
		items.remove(character);
	}
	
	public void update(Entity owner) {
		items.values().forEach(s -> s.getItem().update(owner));
	}
	
	public void addListener(ContainerListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ContainerListener listener) {
		listeners.remove(listener);
	}
	
	public static Container createFromJSON(JSONObject obj) {
		return createFromJSON(Container.class, obj);
	}
	
	public static Container createFromJSON(Class<? extends Container> clazz, JSONObject obj) {
		try {
			Constructor c = ConstructorUtils.getAccessibleConstructor(clazz, String.class);
			Container container = (Container) c.newInstance(obj.getString("name"));
			container.unserialise(obj);
			return container;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			ErrorHandler.error("Error unserialising Container", e);
		}
		
		Container container = new Container(obj.getString("name"));
		container.unserialise(obj);
		return container;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		obj.put("name", name);
		
		JSONObject serialisedItems = new JSONObject();
		items.forEach((key, value) -> {
			JSONObject serialisedItemStack = new JSONObject();
			value.serialise(serialisedItemStack);
			serialisedItems.put(key.toString(), serialisedItemStack);
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
		return getItemStacksOfType(Wieldable.class);
	}
	
	public Map<Character, ItemStack> getComestibles() {
		return getItemStacksOfType(ItemComestible.class);
	}
	
	public Map<Character, ItemStack> getQuaffables() {
		return getItemStacksOfType(ItemQuaffable.class);
	}
	
	public <T> Map<Character, ItemStack> getItemStacksOfType(Class<? extends T> type) {
		return items.entrySet().stream()
			.filter(e -> e.getValue().getItem().getClass().isAssignableFrom(type))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	public List<EventListener> getSubListeners() {
		List<EventListener> subListeners = new ArrayList<>();
		
		items.values().forEach(i -> subListeners.add(i.getItem()));
		
		return subListeners;
	}
	
	public interface ContainerListener {
		void onItemAdd(ContainerEntry entry);
		
		void onItemIncrement(ContainerEntry entry, int amount);
		
		void onItemRemove(ContainerEntry entry);
	}
	
	public class ContainerEntry {
		@Getter	private final Character letter;
		@Getter private ItemStack stack;
		
		public ContainerEntry(Map.Entry<Character, ItemStack> entry) {
			this.letter = entry.getKey();
			this.stack = entry.getValue();
		}
		
		public ContainerEntry(final Character letter, final ItemStack stack) {
			this.letter = letter;
			this.stack = stack;
		}
		
		public Item getItem() {
			return stack.getItem();
		}
		
		public int getCount() {
			return stack.getCount();
		}
		
		public ItemStack setStack(final ItemStack value) {
			final ItemStack oldStack = this.stack;
			this.stack = value;
			items.put(letter, value);
			return oldStack;
		}
	}
}
