package jr.dungeon.items.identity;

import jr.dungeon.items.Item;
import jr.dungeon.items.comestibles.ItemCorpse;
import jr.dungeon.serialisation.Registered;
import jr.language.Noun;
import jr.language.transformers.TransformerType;

/**
 * Aspect of knowing whether a {@link jr.dungeon.items.comestibles.ItemComestible comestible} is rotten or not. This
 * is usually identified automatically when the player realises that something in their inventory 'really stinks'.
 */
@Registered(id="aspectRottenness")
public class AspectRottenness extends Aspect {
	@Override
	public String getName() {
		return "Rottenness";
	}
	
	@Override
	public Noun applyNameTransformers(Item item, Noun name) {
		if (item instanceof ItemCorpse && ((ItemCorpse) item).getRottenness() > 7) {
			name.addInstanceTransformer(Transformer.class, (s, m) -> "rotten " + s);
		}
		
		return name;
	}
	
	public int getNamePriority() {
		return 30;
	}
	
	@Override
	public boolean isPersistent() {
		return false;
	}
	
	public class Transformer implements TransformerType {}
}
