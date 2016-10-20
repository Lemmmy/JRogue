package pw.lemmmy.jrogue.util;

import java.util.Random;

public class Utils {
	private static final Random rand = new Random();

	@SafeVarargs
	public static <T> T randomFrom(T... items) {
		return items[rand.nextInt(items.length)];
	}
}
