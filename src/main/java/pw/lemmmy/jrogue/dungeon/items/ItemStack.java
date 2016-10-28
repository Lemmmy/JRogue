package pw.lemmmy.jrogue.dungeon.items;

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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void addCount(int count) {
		this.count += count;
	}

	public boolean beginsWithVowel() {
		return item.beginsWithVowel();
	}
}
