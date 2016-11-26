package pw.lemmmy.jrogue.rendering.gdx.items;

import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;

public enum ItemMap {
	APPEARANCE_CORPSE(0, 0),

	APPEARANCE_STAFF(0, 1);

	public static final int ITEM_WIDTH = 16;
	public static final int ITEM_HEIGHT = 16;

	private ItemRenderer renderer;

	ItemMap(ItemRenderer renderer) {
		this.renderer = renderer;
	}

	ItemMap(int sheetX, int sheetY) {
		this("items.png", sheetX, sheetY);
	}

	ItemMap(String sheetName, int sheetX, int sheetY) {
		this.renderer = new ItemRendererBasic(sheetName, sheetX, sheetY);
	}

	public ItemAppearance getAppearance() {
		return ItemAppearance.valueOf(name());
	}

	public ItemRenderer getRenderer() {
		return renderer;
	}
}
