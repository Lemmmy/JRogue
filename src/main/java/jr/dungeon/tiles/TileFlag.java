package jr.dungeon.tiles;

public class TileFlag {
	public static final int WALL 				= (int) Math.pow(2, 1),
							FLOOR 				= (int) Math.pow(2, 2),
							INNER_ROOM 			= (int) Math.pow(2, 3),
							SEMI_TRANSPARENT	= (int) Math.pow(2, 4),
							WATER				= (int) Math.pow(2, 5),
							DOOR				= (int) Math.pow(2, 6),
							DOOR_SHUT			= (int) Math.pow(2, 7),
							BUILDABLE			= (int) Math.pow(2, 8);
}
