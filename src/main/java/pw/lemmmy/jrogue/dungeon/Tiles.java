package pw.lemmmy.jrogue.dungeon;

public enum Tiles {
	TILE_GROUND(Solidity.SOLID, true),

	TILE_ROOM_WALL(Solidity.SOLID),
	TILE_ROOM_FLOOR(Solidity.WALK_ON),
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
