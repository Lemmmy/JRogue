package pw.lemmmy.jrogue.dungeon.items.quaffable.potions;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;
import pw.lemmmy.jrogue.dungeon.items.ItemCategory;
import pw.lemmmy.jrogue.dungeon.items.Shatterable;
import pw.lemmmy.jrogue.dungeon.items.quaffable.ItemQuaffable;
import pw.lemmmy.jrogue.utils.RandomUtils;

public class ItemPotion extends ItemQuaffable implements Shatterable {
	private boolean empty = false;
	private BottleType bottleType = BottleType.BOTTLE_LABELLED;
	private PotionType potionType = PotionType.POTION_HEALTH;
	private PotionColour potionColour;
	private float potency = 0.0f;
	
	public ItemPotion() {
		potionColour = RandomUtils.randomFrom(PotionColour.values());
	}
	
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
	
	public PotionColour getPotionColour() {
		return potionColour;
	}
	
	public void setPotionColour(PotionColour potionColour) {
		this.potionColour = potionColour;
	}
	
	public float getPotency() {
		return potency;
	}
	
	public void setPotency(float potency) {
		this.potency = potency;
	}
	
	@Override
	public String getName(LivingEntity observer, boolean requiresCapitalisation, boolean plural) {
		String s = getBeatitudePrefix(observer, requiresCapitalisation);
		
		if (!s.isEmpty() && requiresCapitalisation) {
			requiresCapitalisation = false;
		}
			
		String emptyText = requiresCapitalisation ? "Empty " : "empty ";
		
		if (empty && requiresCapitalisation) {
			requiresCapitalisation = false;
		}
		
		String colourName = "";
		
		if (!empty) {
			colourName = requiresCapitalisation ?
						 StringUtils.capitalize(getPotionColour().getName()) :
						 getPotionColour().getName();
			
			colourName += " ";
		}
		
		s += (empty ? emptyText : "") + colourName + "potion" + (plural ? "s" : "");
		
		return s;
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
	public void quaff(Entity entity) {
		if (empty) {
			return;
		}
		
		if (entity instanceof LivingEntity) {
			potionType.getEffect().apply((LivingEntity) entity, potency);
		}
	}
	
	@Override
	public boolean canQuaff() {
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
		obj.put("colour", potionColour.name());
		obj.put("potency", (double) potency);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		empty = obj.optBoolean("empty", false);
		bottleType = BottleType.valueOf(obj.optString("bottle", "BOTTLE"));
		potionType = PotionType.valueOf(obj.optString("type", "POTION_WATER"));
		potionColour = PotionColour.valueOf(obj.optString("colour", "CLEAR"));
		potency = (float) obj.optDouble("potency", 0.0);
	}
	
	@Override
	public boolean equals(Item other) {
		if (this == other) { return true; }
		if (other == null || getClass() != other.getClass()) { return false; }
		
		ItemPotion that = (ItemPotion) other;
		
		if (empty != that.empty) { return false; }
		if (Float.compare(that.potency, potency) != 0) { return false; }
		if (bottleType != that.bottleType) { return false; }
		if (potionColour != that.potionColour) { return false; }
		return potionType == that.potionType;
	}
	
	@Override
	public int hashCode() {
		int result = (empty ? 1 : 0);
		result = 31 * result + (bottleType != null ? bottleType.hashCode() : 0);
		result = 31 * result + (potionType != null ? potionType.hashCode() : 0);
		result = 31 * result + (potionColour != null ? potionColour.hashCode() : 0);
		result = 31 * result + (potency != +0.0f ? Float.floatToIntBits(potency) : 0);
		return result;
	}
}
