package pw.lemmmy.jrogue.utils;

import com.badlogic.gdx.Input;
import com.github.alexeyr.pcg.Pcg32;
import org.apache.commons.lang3.Range;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Utils {
	public static final char[] INVENTORY_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
		.toCharArray();
	
	public static final Map<Integer, Integer[]> MOVEMENT_KEYS = new HashMap<>();
	public static final Map<Character, Integer[]> MOVEMENT_CHARS = new HashMap<>();
	public static final int[][] DIRECTIONS = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
	private static final Pcg32 rand = new Pcg32();
	private static final Random jrand = new Random();
	
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
	
	@SafeVarargs
	public static <T> T randomFrom(T... items) {
		return items[rand.nextInt(items.length)];
	}
	
	public static <T> T randomFrom(List<T> items) {
		return items.get(rand.nextInt(items.size()));
	}
	
	@SafeVarargs
	public static <T> T jrandomFrom(T... items) {
		return items[jrand.nextInt(items.length)];
	}
	
	public static <T> T jrandomFrom(List<T> items) {
		return items.get(jrand.nextInt(items.size()));
	}
	
	public static int random(int i) {
		return rand.nextInt(i);
	}
	
	public static int random(int min, int max) {
		return rand.nextInt(max - min) + min;
	}
	
	public static int random(Range<Integer> range) {
		return rand.nextInt(range.getMaximum() - range.getMinimum()) + range.getMinimum();
	}
	
	public static int jrandom(Range<Integer> range) {
		return jrand.nextInt(range.getMaximum() - range.getMinimum()) + range.getMinimum();
	}
	
	public static float randomFloat() {
		return rand.nextFloat();
	}
	
	public static float randomFloat(float f) {
		return rand.nextFloat(f);
	}
	
	public static boolean rollD2() {
		return jrand.nextBoolean();
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
			o += rand.nextInt(x) + 1;
		}
		
		return o;
	}
	
	public static int distance(int ax, int ay, int bx, int by) {
		return (int) Math.sqrt(distanceSq(ax, ay, bx, by));
	}
	
	public static float distanceSq(float ax, float ay, float bx, float by) {
		return (ax - bx) * (ax - bx) + (ay - by) * (ay - by);
	}
	
	public static float distance(float ax, float ay, float bx, float by) {
		return (float) Math.sqrt(distanceSq(ax, ay, bx, by));
	}
	
	public static double distance(double ax, double ay, double bx, double by) {
		return Math.sqrt(distanceSq(ax, ay, bx, by));
	}
	
	public static double distanceSq(double ax, double ay, double bx, double by) {
		return (ax - bx) * (ax - bx) + (ay - by) * (ay - by);
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
