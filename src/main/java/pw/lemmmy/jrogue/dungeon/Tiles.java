package pw.lemmmy.jrogue.dungeon;

public enum Tiles {
	TILE_EMPTY(Solidity.SOLID, true),

	TILE_DEBUG_A(Solidity.WALK_ON),
	TILE_DEBUG_B(Solidity.WALK_ON),
	TILE_DEBUG_C(Solidity.WALK_ON),
	TILE_DEBUG_D(Solidity.WALK_ON),
	TILE_DEBUG_E(Solidity.WALK_ON),
	TILE_DEBUG_F(Solidity.WALK_ON),
	TILE_DEBUG_G(Solidity.WALK_ON),
	TILE_DEBUG_H(Solidity.WALK_ON),

	TILE_ROOM_WALL(Solidity.SOLID),
	TILE_ROOM_FLOOR(Solidity.WALK_ON),
	TILE_ROOM_WATER(Solidity.WALK_ON),
	TILE_ROOM_DOOR(Solidity.WALK_THROUGH),

	TILE_CORRIDOR(Solidity.WALK_ON, true);

	private Solidity solidity;
	private boolean buildable;

	Tiles(Solidity solidity) {
		this(solidity, false);
	}

	Tiles(Solidity solidity, boolean buildable) {
		this.solidity = solidity;
		this.buildable = buildable;
	}

	public Solidity getSolidity() {
		return solidity;
	}

	public boolean isBuildable() {
		return buildable;
	}
}
