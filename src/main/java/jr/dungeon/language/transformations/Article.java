package jr.dungeon.language.transformations;

import jr.dungeon.language.Noun;
import org.apache.commons.lang3.StringUtils;

public class Article implements TransformerType {
	public static final Transformer a = (s, m) -> "a " + s;
	
	public static final Transformer an = (s, m) -> "an " + s;
	
	public static final Transformer the = (s, m) -> "the " + s;
	
	public static final Transformer autoA = (s, m) -> StringUtils.startsWithAny("aeiou8", s) ?
													  an.apply(s, m) : a.apply(s, m);
	
	public static void addAIfPossible(Noun n) {
		if (!n.hasInstanceTransformer(Possessive.class) && !n.hasInstanceTransformer(Plural.class)) {
			n.addInstanceTransformer(Article.class, autoA);
		}
	}
	
	public static void addTheIfPossible(Noun n, boolean pluralAware) {
		if (!n.hasInstanceTransformer(Possessive.class)) {
			if (pluralAware) {
				if (n.hasInstanceTransformer(Plural.class)) return;
				if (n.hasInstanceTransformer(Article.class)) return;
			}
			
			n.addInstanceTransformer(Article.class, the);
		}
	}
}
