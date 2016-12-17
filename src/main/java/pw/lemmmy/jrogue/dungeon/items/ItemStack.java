package pw.lemmmy.jrogue.dungeon.items;

import org.json.JSONObject;

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
		if (count > 1) {
			return String.format("%d %s", count, item.getName(false, true));
		} else {
			return item.getName(requiresCapitalisation, false);
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

	public float getWeight() {
		return item.getWeight() * count;
	}

	public boolean beginsWithVowel() {
		return item.beginsWithVowel();
	}

	public void serialise(JSONObject obj) {
		obj.put("count", getCount());

		JSONObject serialisedItem = new JSONObject();
		item.serialise(serialisedItem);
		obj.put("item", serialisedItem);
	}
}
