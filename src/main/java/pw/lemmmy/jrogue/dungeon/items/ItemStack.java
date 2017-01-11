package pw.lemmmy.jrogue.dungeon.items;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.items.comestibles.ItemComestible;
import pw.lemmmy.jrogue.dungeon.items.weapons.ItemWeapon;

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
	
	public String getName(boolean requiresCapitalisation) {
		String prefix = "";

		if (count > 1) {
			requiresCapitalisation = false;
		}

		if (false) { // TODO: If BUC status is known by the player
			if (item.getBUCStatus().equals(Item.BUCStatus.BLESSED)) {
				prefix = prefix + (requiresCapitalisation ? "Blessed" : "blessed") + " ";
				requiresCapitalisation = false;
			}

			if (item.getBUCStatus().equals(Item.BUCStatus.UNCURSED)) {
				prefix = prefix + (requiresCapitalisation ? "Uncursed" : "uncursed") + " ";
				requiresCapitalisation = false;
			}

			if (item.getBUCStatus().equals(Item.BUCStatus.CURSED)) {
				prefix = prefix + (requiresCapitalisation ? "Cursed" : "cursed") + " ";
				requiresCapitalisation = false;
			}
		}

		if (item instanceof ItemComestible) {
			if (((ItemComestible) item).getTurnsEaten() > 0) {
				prefix = prefix + (requiresCapitalisation ? "Partly eaten" : "partly eaten") + " ";
				requiresCapitalisation = false;
			}
		}

		if (item instanceof ItemWeapon) {
			prefix = prefix + String.format("+%d ", ((ItemWeapon) item).getToHitBonus());
			requiresCapitalisation = false; // is this appropriate?
		}

		return (count > 1 ? count + " " : "") + prefix + item.getName(requiresCapitalisation, count > 1);
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
	
	public boolean beginsWithVowel() {
		return item.beginsWithVowel();
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
