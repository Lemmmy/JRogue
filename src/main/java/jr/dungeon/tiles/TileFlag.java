package jr.dungeon.tiles;

public class TileFlag {
	public static final int WALL 				= (int) Math.pow(2, 1),
							FLOOR 				= (int) Math.pow(2, 2),
							INNER_ROOM 			= (int) Math.pow(2, 3),
							SEMI_TRANSPARENT	= (int) Math.pow(2, 4),
							WATER				= (int) Math.pow(2, 5),
							DOOR				= (int) Math.pow(2, 6),
							DOOR_SHUT			= (int) Math.pow(2, 7),
							BUILDABLE			= (int) Math.pow(2, 8),
							DONT_REFLECT		= (int) Math.pow(2, 9),
							STAIRS				= (int) Math.pow(2, 10),
							LADDER				= (int) Math.pow(2, 11),
							UP					= (int) Math.pow(2, 12),
							DOWN				= (int) Math.pow(2, 13),
							CLIMBABLE			= (int) Math.pow(2, 14);
}
