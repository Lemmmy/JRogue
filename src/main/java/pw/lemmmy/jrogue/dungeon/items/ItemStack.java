package pw.lemmmy.jrogue.dungeon.items;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;

import java.util.Optional;

public class ItemStack {
	private Item item;
	private int count;
	
	public ItemStack(Item item) {
		this(item, 1);
	}
	
	public ItemStack(Item item, int count) {
		this.item = item;
		this.count = count;
	}
	
	public Item getItem() {
		return item;
	}
	
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		if (count > 1) {
			return String.format("%d %s", count, item.getName(observer, false, true));
		} else {
			return item.getName(observer, requiresCapitalisation, false);
		}
	}
	
	public ItemAppearance getAppearance() {
		return item.getAppearance();
	}
	
	public ItemCategory getCategory() {
		return item.getCategory();
	}
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public void addCount(int count) {
		this.count += count;
	}
	
	public void subtractCount(int i) {
		count = Math.max(0, count - i);
	}
	
	public float getWeight() {
		return item.getWeight() * count;
	}
	
	public boolean beginsWithVowel(EntityLiving observer) {
		return item.beginsWithVowel(observer);
	}
	
	public static Optional<ItemStack> createFromJSON(JSONObject serialisedItemStack) {
		Optional<Item> item = Item.createFromJSON(serialisedItemStack.getJSONObject("item"));
		
		if (item.isPresent()) {
			int count = serialisedItemStack.getInt("count");
			
			ItemStack itemStack = new ItemStack(item.get(), count);
			return Optional.of(itemStack);
		}
		
		return Optional.empty();
	}
	
	public void serialise(JSONObject obj) {
		obj.put("count", getCount());
		
		JSONObject serialisedItem = new JSONObject();
		item.serialise(serialisedItem);
		obj.put("item", serialisedItem);
	}
}