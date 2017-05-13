package jr.dungeon.language.mutations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http://www.englisch-hilfen.de/en/grammar/plural.htm
 */
public class Plural implements MutationType {
	/**
	 * 1.1, 1.3.2, 1.4.1, 1.5.1
	 */
	public static final Mutation s = (s, m) -> s + "s";
	
	private static final Pattern iesPattern = Pattern.compile("([^aeiou])?y$");
	/**
	 * 1.3.1
	 */
	public static final Mutation ies = (s, m) -> {
		Matcher matcher = iesPattern.matcher(s);
		String consonant = matcher.find() ? matcher.group(1) : "";
		return matcher.replaceAll("") + consonant + "ies";
	};
	
	private static final Pattern esPattern = Pattern.compile("(?:[zs]h?|ch|j)e?$");
	/**
	 * 1.2
	 */
	public static final Mutation es = (s, m) -> esPattern.matcher(s).replaceAll("") + "es";
	
	private static final Pattern vesPattern = Pattern.compile("f$");
	/**
	 * 1.4.2
	 */
	public static final Mutation ves = (s, m) -> vesPattern.matcher(s).replaceAll("ves");
	
	/**
	 * 1.5.2
	 */
	public static final Mutation oes = (s, m) -> s + "es";
	
	public static String optionalGroup(Matcher matcher, int group) {
		try {
			return matcher.group(group);
		} catch (Exception ignored) {
			return "";
		}
	}
}
