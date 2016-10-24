package pw.lemmmy.jrogue.utils;

import java.util.List;
import java.util.Random;

public class Utils {
	public static final int[][] DIRECTIONS = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

	private static final Random rand = new Random();

	@SafeVarargs
	public static <T> T randomFrom(T... items) {
		return items[rand.nextInt(items.length)];
	}

	public static <T> T randomFrom(List<T> items) {
		return items.get(rand.nextInt(items.size()));
	}
}
