package jr.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

import java.util.HashMap;
import java.util.Map;

public class Utils {
	public static final char[] INVENTORY_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
		.toCharArray();
	
	public static final Map<Integer, VectorInt> MOVEMENT_KEYS = new HashMap<>();
	public static final Map<Character, VectorInt> MOVEMENT_CHARS = new HashMap<>();

	public static final VectorInt[] DIRECTIONS = new VectorInt[] {
		new VectorInt(1, 0), new VectorInt(-1, 0),
		new VectorInt(0, -1), new VectorInt(0, 1)
	};

	public static final VectorInt[] OCT_DIRECTIONS = new VectorInt[] {
		new VectorInt(-1, 1), new VectorInt(0, 1), new VectorInt(1, 1),
		new VectorInt(-1, 0), new VectorInt(1, 0),
		new VectorInt(-1, -1), new VectorInt(0, -1), new VectorInt(1, -1)
	};
	
	private static final Map<Integer, com.badlogic.gdx.graphics.Color> DUMMY_COLOURS = new HashMap<>();
	
	static {
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_1, new VectorInt(-1, -1));
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_2, new VectorInt(0, -1));
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_3, new VectorInt(1, -1));
		
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_4, new VectorInt(-1, 0));
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_6, new VectorInt(1, 0));
		
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_7, new VectorInt(-1, 1));
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_8, new VectorInt(0, 1));
		MOVEMENT_KEYS.put(Input.Keys.NUMPAD_9, new VectorInt(1, 1));
	}
	
	static {
		MOVEMENT_CHARS.put('1', new VectorInt(-1, -1));
		MOVEMENT_CHARS.put('2', new VectorInt(0, -1));
		MOVEMENT_CHARS.put('3', new VectorInt(1, -1));
		
		MOVEMENT_CHARS.put('4', new VectorInt(-1, 0));
		MOVEMENT_CHARS.put('6', new VectorInt(1, 0));
		
		MOVEMENT_CHARS.put('7', new VectorInt(-1, 1));
		MOVEMENT_CHARS.put('8', new VectorInt(0, 1));
		MOVEMENT_CHARS.put('9', new VectorInt(1, 1));
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
	
	public static Color colourToGdx(Colour colour, int dummyID) {
		if (DUMMY_COLOURS.containsKey(dummyID)) {
			Color c = DUMMY_COLOURS.get(dummyID);
			c.set(colour.r, colour.g, colour.b, colour.a);
			return c;
		} else {
			Color c = new Color(colour.r, colour.g, colour.b, colour.a);
			DUMMY_COLOURS.put(dummyID, c);
			return c;
		}
	}
	
	public static float easeIn(float time, float start, float change, float duration) {
		return change * (time /= duration) * time * time + start;
	}
	
	public static float easeOut(float time, float start, float change, float duration) {
		return change * ((time = time / duration - 1) * time * time + 1) + start;
	}
	
	public static float easeInOut(float time, float start, float change, float duration) {
		if ((time /= duration / 2) < 1) return change / 2 * time * time * time + start;
		return change / 2 * ((time -= 2) * time * time + 2) + start;
	}
}
