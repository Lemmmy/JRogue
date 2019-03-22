package jr.dungeon.items.quaffable.potions;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.items.ItemCategory;
import jr.dungeon.items.Shatterable;
import jr.dungeon.items.quaffable.ItemQuaffable;
import jr.dungeon.serialisation.Registered;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.transformers.TransformerType;
import jr.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

@Getter
@Setter
@Registered(id="itemPotion")
public class ItemPotion extends ItemQuaffable implements Shatterable {
	@Expose private boolean empty = false;
	@Expose private BottleType bottleType = BottleType.BOTTLE_LABELLED;
	@Expose private PotionType potionType = PotionType.POTION_HEALTH;
	@Expose private PotionColour potionColour;
	@Expose private float potency = 0.0f;
	
	public ItemPotion() {
		potionColour = RandomUtils.randomFrom(PotionColour.values());
	}
	
	@Override
	public Noun getName(EntityLiving observer) {
		if (empty) {
			return Lexicon.glassBottle.clone();
		} else {
			String colourName = getPotionColour().getName();
			
			return Lexicon.potion.clone()
				.addInstanceTransformer(ColourTransformer.class, (s, m) -> colourName + " " + s);
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
	
	@Override
	public ToStringBuilder toStringBuilder() {
		return super.toStringBuilder()
			.append("empty", empty)
			.append("potency", potency)
			.append("bottleType", bottleType.name().toLowerCase())
			.append("potionType", bottleType.name().toLowerCase())
			.append("potionColour", potionColour.name().toLowerCase());
	}
	
	public class ColourTransformer implements TransformerType {}
}
