package jr.dungeon.items;

import jr.dungeon.entities.EntityLiving;
import jr.language.Noun;
import jr.language.transformations.Plural;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.Optional;

@Getter
public class ItemStack {
	private Item item;
	@Setter private int count;
	
	public ItemStack(Item item) {
		this(item, 1);
	}
	
	public ItemStack(Item item, int count) {
		this.item = item;
		this.count = count;
	}
	
	public Noun getName(EntityLiving observer) {
		return Plural.addCount(item.getName(observer), count, item.isNonCountable());
	}
	
	public ItemAppearance getAppearance() {
		return item.getAppearance();
	}
	
	public ItemCategory getCategory() {
		return item.getCategory();
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