package jr.utils;

import java.util.LinkedList;
import java.util.List;

public class Cardinal {
	public static final int NORTH = 0b0001;
	public static final int SOUTH = 0b0010;
	public static final int EAST  = 0b0100;
	public static final int WEST  = 0b1000;
	
	public static final int NORTH_WEST = NORTH | WEST;
	public static final int NORTH_EAST = NORTH | EAST;
	public static final int SOUTH_WEST = SOUTH | WEST;
	public static final int SOUTH_EAST = SOUTH | EAST;
	
	public static String nameOf(int direction) {
		if (direction == -1) {
			return "Invalid";
		}
		
		List<String> words = new LinkedList<>();
		
		if ((direction & NORTH) == NORTH) {
			words.add("North");
		} else if ((direction & SOUTH) == SOUTH) {
			words.add("South");
		}
		
		if ((direction & WEST) == WEST) {
			words.add("West");
		} else if ((direction & EAST) == EAST) {
			words.add("East");
		}
		
		return words.stream()
			.reduce("", (a, b) -> a + " " + b)
			.trim();
	}
}
