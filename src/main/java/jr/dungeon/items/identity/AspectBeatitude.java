package jr.dungeon.items.identity;

import jr.dungeon.language.Noun;
import jr.dungeon.language.transformations.TransformerType;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

/**
 * Beatitude aspect - the 'holiness' of an item.
 *
 * @see Beatitude
 * @see Aspect
 */
@Getter
@Setter
public class AspectBeatitude extends Aspect {
	private Beatitude beatitude = Beatitude.UNCURSED;
	
	@Override
	public String getName() {
		return "Beatitude";
	}
	
	@Override
	public Noun applyNameTransformers(Noun name) {
		return name.addInstanceTransformer(Transformer.class, (s, m) -> beatitude.name().toLowerCase());
	}
	
	@Override
	public boolean isPersistent() {
		return false;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		obj.put("beatitude", beatitude.name());
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		beatitude = Beatitude.valueOf(obj.optString("beatitude", Beatitude.UNCURSED.name()));
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		
		AspectBeatitude that = (AspectBeatitude) o;
		
		return beatitude == that.beatitude;
	}
	
	@Override
	public int hashCode() {
		return beatitude != null ? beatitude.hashCode() : 0;
	}
	
	/**
	 * Beatitude - the 'holiness' of an item - whether its blessed, uncursed or cursed.
	 */
	public enum Beatitude {
		/**
		 * The item is blessed - it has special perks and properties that the item usually doesn't have.
		 */
		BLESSED,
		/**
		 * The item is uncursed, or neutral. It has its regular properties.
		 */
		UNCURSED,
		/**
		 * The item is cursed - it has negative properties that are almost always a problem to the player.
		 */
		CURSED
	}
	
	public class Transformer implements TransformerType {}
}
