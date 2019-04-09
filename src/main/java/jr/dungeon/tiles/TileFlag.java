package jr.dungeon.tiles;

import static jr.utils.QuickMaths.ipow;

public class TileFlag {
    public static final int WALL             = ipow(2, 1),
                            FLOOR            = ipow(2, 2),
                            INNER_ROOM       = ipow(2, 3),
                            SEMI_TRANSPARENT = ipow(2, 4),
                            WATER            = ipow(2, 5),
                            DOOR             = ipow(2, 6),
                            DOOR_SHUT        = ipow(2, 7),
                            BUILDABLE        = ipow(2, 8),
                            DONT_REFLECT     = ipow(2, 9),
                            STAIRS           = ipow(2, 10),
                            LADDER           = ipow(2, 11),
                            UP               = ipow(2, 12),
                            DOWN             = ipow(2, 13),
                            CLIMBABLE        = ipow(2, 14),
                            SPAWNABLE        = ipow(2, 15);
}
