package pw.lemmmy.jrogue.rendering.gdx.items;

import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;

public enum ItemMap {
	APPEARANCE_CORPSE(0, 0),

	APPEARANCE_STAFF(0, 1),
	APPEARANCE_DAGGER(new ItemRendererSword(18, 2)),
	APPEARANCE_SHORTSWORD(new ItemRendererSword(0, 2)),
	APPEARANCE_LONGSWORD(new ItemRendererSword(9, 2)),

	APPEARANCE_GOLD(new ItemRendererGold()),

	APPEARANCE_GEM_WHITE(11, 7),
	APPEARANCE_GEM_RED(12, 7),
	APPEARANCE_GEM_ORANGE(13, 7),
	APPEARANCE_GEM_YELLOW(14, 7),
	APPEARANCE_GEM_LIME(15, 7),
	APPEARANCE_GEM_GREEN(16, 7),
	APPEARANCE_GEM_CYAN(18, 7),
	APPEARANCE_GEM_BLUE(19, 7),
	APPEARANCE_GEM_PURPLE(20, 7),
	APPEARANCE_GEM_PINK(21, 7),
	APPEARANCE_GEM_BLACK(22, 7);

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
