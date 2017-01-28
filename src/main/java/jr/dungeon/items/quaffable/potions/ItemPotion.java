package jr.dungeon.items.quaffable.potions;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.items.ItemCategory;
import jr.dungeon.items.Shatterable;
import jr.dungeon.items.quaffable.ItemQuaffable;
import jr.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

@Getter
@Setter
public class ItemPotion extends ItemQuaffable implements Shatterable {
	private boolean empty = false;
	private BottleType bottleType = BottleType.BOTTLE_LABELLED;
	private PotionType potionType = PotionType.POTION_HEALTH;
	private PotionColour potionColour;
	private float potency = 0.0f;
	
	public ItemPotion() {
		potionColour = RandomUtils.randomFrom(PotionColour.values());
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation, boolean plural) {
		String s = getBeatitudePrefix(observer, requiresCapitalisation);
		
		if (!s.isEmpty() && requiresCapitalisation) {
			requiresCapitalisation = false;
		}
		
		if (empty) {
			s += requiresCapitalisation ? "Glass bottle" : "glass bottle";
			
			return s;
		} else {
			String colourName = requiresCapitalisation ?
							 StringUtils.capitalize(getPotionColour().getName()) :
							 getPotionColour().getName();
			
			s += colourName + " potion" + (plural ? "s" : "");
			
			return s;
		}
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
	public void quaff(EntityLiving quaffer) {
		if (empty) {
			return;
		}
		
		if (quaffer != null) {
			potionType.getEffect().apply(quaffer, potency);
		}
	}
	
	@Override
	public boolean canQuaff(EntityLiving quaffer) {
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
		int result = empty ? 1 : 0;
		result = 31 * result + (bottleType != null ? bottleType.hashCode() : 0);
		result = 31 * result + (potionType != null ? potionType.hashCode() : 0);
		result = 31 * result + (potionColour != null ? potionColour.hashCode() : 0);
		result = 31 * result + (potency != +0.0f ? Float.floatToIntBits(potency) : 0);
		return result;
	}
}
