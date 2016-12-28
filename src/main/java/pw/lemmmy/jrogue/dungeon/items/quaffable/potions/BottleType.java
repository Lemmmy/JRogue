package pw.lemmmy.jrogue.dungeon.items.quaffable.potions;

import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;

public enum BottleType {
	BOTTLE(ItemAppearance.APPEARANCE_POTION, ItemAppearance.APPEARANCE_POTION_EMPTY),
	BOTTLE_LABELLED(ItemAppearance.APPEARANCE_POTION_LABEL, ItemAppearance.APPEARANCE_POTION_LABEL_EMPTY),
	BOTTLE_CORK(ItemAppearance.APPEARANCE_POTION_CORK, ItemAppearance.APPEARANCE_POTION_CORK_EMPTY),
	BOTTLE_CORK_LABELLED(
		ItemAppearance.APPEARANCE_POTION_CORK_LABEL,
		ItemAppearance.APPEARANCE_POTION_CORK_LABEL_EMPTY
	),
	BULB(ItemAppearance.APPEARANCE_POTION_FAT, ItemAppearance.APPEARANCE_POTION_FAT_EMPTY);
	
	private final ItemAppearance appearance;
	private final ItemAppearance appearanceEmpty;
	
	BottleType(ItemAppearance appearance, ItemAppearance appearanceEmpty) {
		this.appearance = appearance;
		this.appearanceEmpty = appearanceEmpty;
	}
	
	public ItemAppearance getAppearance(boolean empty) {
		return empty ? appearanceEmpty : appearance;
	}
}
