package pw.lemmmy.jrogue.utils;

import java.util.HashMap;
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

	public int roll(int x) {
		return roll(1, x);
	}

	public int roll(int a, int x) {
		return roll(a, x, 0);
	}

	public int roll(int a, int x, int b) {
		int o = b;

		for (int i = 0; i < a; i++) {
			o += rand.nextInt(x);
		}

		return o;
	}

	public static com.badlogic.gdx.graphics.Color awtColourToGdx(java.awt.Color colour) {
		return new com.badlogic.gdx.graphics.Color(
			(float) colour.getRed() / 255.0f,
			(float) colour.getGreen() / 255.0f,
			(float) colour.getBlue() / 255.0f,
			(float) colour.getAlpha() / 255.0f
		);
	}
}
