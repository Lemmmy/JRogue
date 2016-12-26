package pw.lemmmy.jrogue.dungeon.items;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.potions.PotionType;

public class ItemPotion extends ItemDrinkable {
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
    private PotionType potionType = PotionType.POTION_HEALTH;
    private float potency = 0.0f;

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

    public PotionType getPotionType() {
        return potionType;
    }

    public void setPotionType(PotionType potionType) {
        this.potionType = potionType;
    }

    public float getPotency() {
        return potency;
    }

    public void setPotency(float potency) {
        this.potency = potency;
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
    public void drink(Entity entity) {
        if (empty) {
            return;
        }

        if (entity instanceof LivingEntity) {
            potionType.getEffect().apply((LivingEntity) entity, potency);
        }
    }

    @Override
    public boolean canDrink() {
        return !empty;
    }

    @Override
    public ItemCategory getCategory() {
        return ItemCategory.POTION;
    }

    @Override
    public void serialise(JSONObject obj) {
        super.serialise(obj);

        obj.put("empty", empty);
        obj.put("bottle", bottleType.name());
        obj.put("type", potionType.name());
        obj.put("potency", (double)potency);
    }

    @Override
    public void unserialise(JSONObject obj) {
        super.unserialise(obj);

        empty = obj.optBoolean("empty", false);
        bottleType = BottleType.valueOf(obj.optString("bottle", "BOTTLE"));
        potionType = PotionType.valueOf(obj.optString("type", "POTION_WATER"));
        potency = (float)obj.optDouble("potency", 0.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemPotion that = (ItemPotion) o;

        if (empty != that.empty) return false;
        if (Float.compare(that.potency, potency) != 0) return false;
        if (bottleType != that.bottleType) return false;
        return potionType == that.potionType;
    }

    @Override
    public int hashCode() {
        int result = (empty ? 1 : 0);
        result = 31 * result + (bottleType != null ? bottleType.hashCode() : 0);
        result = 31 * result + (potionType != null ? potionType.hashCode() : 0);
        result = 31 * result + (potency != +0.0f ? Float.floatToIntBits(potency) : 0);
        return result;
    }
}
