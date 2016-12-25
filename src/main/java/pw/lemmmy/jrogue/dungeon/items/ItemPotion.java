package pw.lemmmy.jrogue.dungeon.items;

import org.json.JSONObject;

public class ItemPotion extends Item {
    public enum BottleType {
        BOTTLE(ItemAppearance.APPEARANCE_POTION, ItemAppearance.APPEARANCE_POTION_EMPTY),
        BOTTLE_LABELLED(ItemAppearance.APPEARANCE_POTION_LABEL, ItemAppearance.APPEARANCE_POTION_LABEL_EMPTY),
        BOTTLE_CORK(ItemAppearance.APPEARANCE_POTION_CORK, ItemAppearance.APPEARANCE_POTION_CORK_EMPTY),
        BOTTLE_CORK_LABELLED(ItemAppearance.APPEARANCE_POTION_CORK_LABEL, ItemAppearance.APPEARANCE_POTION_CORK_LABEL_EMPTY),
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

    private boolean empty = false;
    private BottleType bottleType = BottleType.BOTTLE_LABELLED;

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public BottleType getBottleType() {
        return bottleType;
    }

    public void setBottleType(BottleType type) {
        this.bottleType = type;
    }

    @Override
    public String getName(boolean requiresCapitalisation, boolean plural) {
        String emptyText = requiresCapitalisation ? "Empty " : "empty ";
        return (empty ? emptyText : "") + (requiresCapitalisation ? "Potion" : "potion") + (plural ? "s" : "");
    }

    @Override
    public float getWeight() {
        return 2.0f;
    }

    @Override
    public ItemAppearance getAppearance() {
        return bottleType.getAppearance(empty);
    }

    @Override
    public ItemCategory getCategory() {
        return ItemCategory.POTION;
    }

    @Override
    public void serialise(JSONObject obj) {
        super.serialise(obj);

        obj.put("empty", empty);
        obj.put("type", bottleType.name());
    }

    @Override
    public void unserialise(JSONObject obj) {
        super.unserialise(obj);

        empty = obj.optBoolean("empty", false);
        bottleType = BottleType.valueOf(obj.optString("type", "BOTTLE"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemPotion that = (ItemPotion) o;

        if (empty != that.empty) return false;
        return bottleType == that.bottleType;
    }

    @Override
    public int hashCode() {
        int result = (empty ? 1 : 0);
        result = 31 * result + (bottleType != null ? bottleType.hashCode() : 0);
        return result;
    }
}
