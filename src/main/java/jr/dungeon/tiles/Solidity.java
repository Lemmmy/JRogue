package jr.dungeon.tiles;

public enum Solidity {
	SOLID, // walls etc
	WALK_ON, // ground
	WALK_THROUGH, // disturbs automove - doors and such
	WATER // the player can swim in it
}
