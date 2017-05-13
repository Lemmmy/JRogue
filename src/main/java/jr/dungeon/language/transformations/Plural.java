package jr.dungeon.language.transformations;

import jr.dungeon.language.Noun;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http://www.englisch-hilfen.de/en/grammar/plural.htm
 */
public class Plural implements TransformerType {
	/**
	 * 1.1, 1.3.2, 1.4.1, 1.5.1
	 */
	public static final Transformer s = (s, m) -> s + "s";
	
	private static final Pattern iesPattern = Pattern.compile("([^aeiou])?y$");
	/**
	 * 1.3.1
	 */
	public static final Transformer ies = (s, m) -> {
		Matcher matcher = iesPattern.matcher(s);
		String consonant = matcher.find() ? matcher.group(1) : "";
		return matcher.replaceAll("") + consonant + "ies";
	};
	
	private static final Pattern esPattern = Pattern.compile("(?:[zs]h?|ch|j)e?$");
	/**
	 * 1.2
	 */
	public static final Transformer es = (s, m) -> esPattern.matcher(s).replaceAll("") + "es";
	
	private static final Pattern vesPattern = Pattern.compile("f$");
	/**
	 * 1.4.2
	 */
	public static final Transformer ves = (s, m) -> vesPattern.matcher(s).replaceAll("ves");
	
	/**
	 * 1.5.2
	 */
	public static final Transformer oes = (s, m) -> s + "es";
	
	public static void addCount(Noun n, int count) {
		if (count == 1) {
			n.addInstanceTransformer(Article.class, Article.a);
		} else {
			n.addInstanceTransformer(Count.class, Count.build(count))
			 .addInstanceTransformer(Plural.class);
		}
	}
	
}
