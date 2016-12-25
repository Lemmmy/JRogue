package pw.lemmmy.jrogue.dungeon.items;

import com.badlogic.gdx.graphics.Color;
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

    public enum PotionType {
        WATER(Color.CYAN),
        HEALTH(Color.RED),
        MAGIC(Color.BLUE);

        private final Color colour;

        PotionType(Color colour) {
            this.colour = colour;
        }

        public Color getColour() {
            return colour;
        }
    }

    private boolean empty = false;
    private BottleType bottleType = BottleType.BOTTLE_LABELLED;
    private PotionType potionType = PotionType.HEALTH;

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public BottleType getBottleType() {
        return bottleType;
    }

    public PotionType getPotionType() {
        return potionType;
    }

    public void setPotionType(PotionType potionType) {
        this.potionType = potionType;
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
        obj.put("effect", potionType.name());
    }

    @Override
    public void unserialise(JSONObject obj) {
        super.unserialise(obj);

        empty = obj.optBoolean("empty", false);
        bottleType = BottleType.valueOf(obj.optString("type", "BOTTLE"));
        potionType = PotionType.valueOf(obj.optString("effect", "WATER"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemPotion that = (ItemPotion) o;

        if (empty != that.empty) return false;
        if (bottleType != that.bottleType) return false;
        return potionType == that.potionType;
    }

    @Override
    public int hashCode() {
        int result = (empty ? 1 : 0);
        result = 31 * result + (bottleType != null ? bottleType.hashCode() : 0);
        result = 31 * result + (potionType != null ? potionType.hashCode() : 0);
        return result;
    }
}
