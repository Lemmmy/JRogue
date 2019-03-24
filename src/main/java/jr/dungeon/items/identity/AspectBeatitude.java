package jr.dungeon.items.identity;

import com.google.gson.annotations.Expose;
import jr.dungeon.items.Item;
import jr.dungeon.serialisation.Registered;
import jr.language.Noun;
import jr.language.transformers.TransformerType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Beatitude aspect - the 'holiness' of an item.
 *
 * @see Beatitude
 * @see Aspect
 */
@Getter
@Setter
@Registered(id="aspectBeatitude")
public class AspectBeatitude extends Aspect {
	@Expose private Beatitude beatitude = Beatitude.UNCURSED;
	
	@Override
	public String getName() {
		return "Beatitude";
	}
	
	@Override
	public Noun applyNameTransformers(Item item, Noun name) {
		return name.addInstanceTransformer(Transformer.class, (s, m) -> beatitude.name().toLowerCase());
	}
	
	@Override
	public boolean isPersistent() {
		return false;
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
	
	@Override
	public ToStringBuilder toStringBuilder() {
		return super.toStringBuilder()
			.append("beatitude", beatitude.name().toLowerCase());
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
