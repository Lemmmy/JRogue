package jr.language.transformations;

import jr.language.Noun;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Plural implements TransformerType {
	public static final Transformer none = (s, m) -> s;
	
	public static final Transformer s = (s, m) -> s + "s";
	
	private static final Pattern iesPattern = Pattern.compile("([^aeiou])?y$");
	public static final Transformer ies = (s, m) -> {
		Matcher matcher = iesPattern.matcher(s);
		String consonant = matcher.find() ? matcher.group(1) : "";
		return matcher.replaceAll("") + consonant + "ies";
	};
	
	private static final Pattern esPattern = Pattern.compile("(?:[zs]h?|ch|[jo])e?$");
	public static final Transformer es = (s, m) -> esPattern.matcher(s).replaceAll("") + "es";
	
	private static final Pattern vesPattern = Pattern.compile("f?f$");
	public static final Transformer ves = (s, m) -> vesPattern.matcher(s).replaceAll("ves");
	
	public static final Transformer oes = (s, m) -> s + "es";
	
	private static final Pattern ofPattern = Pattern.compile("(\\b\\w+\\b) of (\\b\\w+\\b)");
	
	public static final Transformer auto = (s, m) -> {
		Matcher ofMatcher = ofPattern.matcher(s);
		
		if (ofMatcher.matches()) {
			return Plural.auto.apply(ofMatcher.group(1), m) + " of " + ofMatcher.group(2);
		} else {
			if (vesPattern.matcher(s).matches()) return ves.apply(s, m);
			if (esPattern.matcher(s).matches()) return es.apply(s, m);
			if (iesPattern.matcher(s).matches()) return ies.apply(s, m);
			return Plural.s.apply(s, m);
		}
	};
	
	public static Noun addCount(Noun n, int count) {
		return addCount(n, count, false);
	}
	
	public static Noun addCount(Noun n, int count, boolean isNonCountable) {
		if (isNonCountable) {
			n.addInstanceTransformer(Count.class, Count.build(count));
		} else {
			if (count == 1) {
				n.addInstanceTransformer(Article.class, Article.autoA);
			} else if (count > 1) {
				n.addInstanceTransformer(Count.class, Count.build(count))
					.addInstanceTransformer(Plural.class);
			}
		}
		
		return n;
	}
}
