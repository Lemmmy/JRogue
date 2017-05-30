package jr.language.transformers;

import jr.language.Noun;
import jr.language.Pronoun;
import org.apache.commons.lang3.StringUtils;

public class Article implements TransformerType {
	public static final Transformer a = (s, m) -> "a " + s;
	
	public static final Transformer an = (s, m) -> "an " + s;
	
	public static final Transformer the = (s, m) -> "the " + s;
	
	public static final Transformer autoA = (s, m) -> StringUtils.startsWithAny(s, "aeiou8".split("(?!^)")) ?
													  an.apply(s, m) : a.apply(s, m);
	
	public static Noun addAIfPossible(Noun n) {
		if (!n.hasInstanceTransformer(Possessive.class) && !n.hasInstanceTransformer(Plural.class)) {
			n.addInstanceTransformer(Article.class, autoA);
		}
		
		return n;
	}
	
	public static Noun addTheIfPossible(Noun n, boolean pluralAware) {
		if (n instanceof Pronoun) return n; // articles don't follow pronouns
		
		if (!n.hasInstanceTransformer(Possessive.class)) {
			if (pluralAware) {
				if (n.hasInstanceTransformer(Plural.class)) return n;
				if (n.hasInstanceTransformer(Article.class)) return n;
			}
			
			n.addInstanceTransformer(Article.class, the);
		}
		
		return n;
	}
}
