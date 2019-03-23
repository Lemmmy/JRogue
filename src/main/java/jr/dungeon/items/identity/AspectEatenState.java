package jr.dungeon.items.identity;

import jr.dungeon.items.Item;
import jr.dungeon.items.comestibles.ItemComestible;
import jr.dungeon.serialisation.Registered;
import jr.language.Noun;
import jr.language.transformers.TransformerType;

@Registered(id="aspectEatenState")
public class AspectEatenState extends Aspect {
	@Override
	public String getName() {
		return "Eaten State";
	}
	
	@Override
	public Noun applyNameTransformers(Item item, Noun name) {
		if (item instanceof ItemComestible && ((ItemComestible) item).getEatenState() == ItemComestible.EatenState.PARTLY_EATEN) {
			name.addInstanceTransformer(Transformer.class, (s, m) -> "partly eaten " + s);
		}
		
		return name;
	}
	
	public int getNamePriority() {
		return 20;
	}
	
	@Override
	public boolean isPersistent() {
		return false;
	}
	
	public class Transformer implements TransformerType {}
}
