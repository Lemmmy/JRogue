package pw.lemmmy.jrogue.utils;

import com.badlogic.gdx.Input;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
	public static final Map<Integer, Integer[]> MOVEMENT_KEYS = new HashMap<>();

	static {
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_1, new Integer[]{-1, 1});
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_2, new Integer[]{0, 1});
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_3, new Integer[]{1, 1});

		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_4, new Integer[]{-1, 0});
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_6, new Integer[]{1, 0});

		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_7, new Integer[]{-1, -1});
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_8, new Integer[]{0, -1});
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_9, new Integer[]{1, -1});
	}

	public static final Map<Character, Integer[]> MOVEMENT_CHARS = new HashMap<>();

	static {
		MOVEMENT_CHARS.put('1', new Integer[]{-1, 1});
		MOVEMENT_CHARS.put('2', new Integer[]{0, 1});
		MOVEMENT_CHARS.put('3', new Integer[]{1, 1});

		MOVEMENT_CHARS.put('4', new Integer[]{-1, 0});
		MOVEMENT_CHARS.put('6', new Integer[]{1, 0});

		MOVEMENT_CHARS.put('7', new Integer[]{-1, -1});
		MOVEMENT_CHARS.put('8', new Integer[]{0, -1});
		MOVEMENT_CHARS.put('9', new Integer[]{1, -1});
	}

	public static final int[][] DIRECTIONS = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

	private static final MersenneTwister rand = new MersenneTwister();

	@SafeVarargs
	public static <T> T randomFrom(T... items) {
		return items[rand.nextInt(items.length)];
	}

	public static <T> T randomFrom(List<T> items) {
		return items.get(rand.nextInt(items.size()));
	}

	public static com.badlogic.gdx.graphics.Color awtColourToGdx(java.awt.Color colour) {
		return new com.badlogic.gdx.graphics.Color(
			(float) colour.getRed() / 255.0f,
			(float) colour.getGreen() / 255.0f,
			(float) colour.getBlue() / 255.0f,
			(float) colour.getAlpha() / 255.0f
		);
	}

	public static int roll(int x) {
		return roll(1, x);
	}

	public static int roll(int a, int x) {
		return roll(a, x, 0);
	}

	public static int roll(int a, int x, int b) {
		int o = b;

		for (int i = 0; i < a; i++) {
			o += rand.nextInt(x);
		}

		return o;
	}

	public static int distanceSq(int ax, int ay, int bx, int by) {
		return (ax - bx) * (ax - bx) + (ay - by) * (ay - by);
	}

	public static float distanceSq(float ax, float ay, float bx, float by) {
		return (ax - bx) * (ax - bx) + (ay - by) * (ay - by);
	}

	public static double distanceSq(double ax, double ay, double bx, double by) {
		return (ax - bx) * (ax - bx) + (ay - by) * (ay - by);
	}

	public static int distance(int ax, int ay, int bx, int by) {
		return (int) Math.sqrt(distanceSq(ax, ay, bx, by));
	}

	public static float distance(float ax, float ay, float bx, float by) {
		return (float) Math.sqrt(distanceSq(ax, ay, bx, by));
	}

	public static double distance(double ax, double ay, double bx, double by) {
		return Math.sqrt(distanceSq(ax, ay, bx, by));
	}
}
