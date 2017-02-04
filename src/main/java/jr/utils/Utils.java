package jr.utils;

import com.badlogic.gdx.Input;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Utils {
	public static final char[] INVENTORY_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
		.toCharArray();
	
	public static final Map<Integer, Integer[]> MOVEMENT_KEYS = new HashMap<>();
	public static final Map<Character, Integer[]> MOVEMENT_CHARS = new HashMap<>();
	public static final int[][] DIRECTIONS = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
	
	private static final Map<Integer, com.badlogic.gdx.graphics.Color> DUMMY_COLOURS = new HashMap<>();
	
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
	
	public static int chebyshevDistance(Point a, Point b) {
		return chebyshevDistance(a.getX(), a.getY(), b.getX(), b.getY());
	}
	
	public static int chebyshevDistance(int ax, int ay, int bx, int by) {
		int dx = Math.abs(ax - bx);
		int dy = Math.abs(ay - by);
		
		return Math.max(dy, dx);
	}
	
	public static float octileDistance(int ax, int ay, int bx, int by, float d, float d2) {
		int dx = Math.abs(ax - bx);
		int dy = Math.abs(ay - by);
		
		return d * (dx + dy) + (d2 - 2 * d) * Math.min(dx, dy);
	}
	
	public static com.badlogic.gdx.graphics.Color awtColourToGdx(java.awt.Color colour, int dummyID) {
		if (DUMMY_COLOURS.containsKey(dummyID)) {
			com.badlogic.gdx.graphics.Color c = DUMMY_COLOURS.get(dummyID);
			
			c.set(
				(float) colour.getRed() / 255.0f,
				(float) colour.getGreen() / 255.0f,
				(float) colour.getBlue() / 255.0f,
				(float) colour.getAlpha() / 255.0f
			);
			
			return c;
		} else {
			com.badlogic.gdx.graphics.Color c = new com.badlogic.gdx.graphics.Color(
				(float) colour.getRed() / 255.0f,
				(float) colour.getGreen() / 255.0f,
				(float) colour.getBlue() / 255.0f,
				(float) colour.getAlpha() / 255.0f
			);
			
			DUMMY_COLOURS.put(dummyID, c);
			
			return c;
		}
	}

	public static Color mixColours(Color colour1, Color colour2) {
		final float r1 = colour1.getRed() / 255.0f;
		final float g1 = colour1.getGreen() / 255.0f;
		final float b1 = colour1.getBlue() / 255.0f;
		final float a1 = colour1.getAlpha() / 255.0f;

		final float r2 = colour2.getRed() / 255.0f;
		final float g2 = colour2.getGreen() / 255.0f;
		final float b2 = colour2.getBlue() / 255.0f;
		final float a2 = colour1.getAlpha() / 255.0f;

		return new Color(
			(int)(r1 * r2 * 255),
			(int)(g1 * g2 * 255),
			(int)(b1 * b2 * 255),
			(int)(a1 * a2 * 255)
		);
	}
}
