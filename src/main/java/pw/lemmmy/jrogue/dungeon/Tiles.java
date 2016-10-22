package pw.lemmmy.jrogue.dungeon;

public enum Tiles {
	TILE_GROUND(Solidity.SOLID),

	TILE_ROOM_WALL(Solidity.SOLID),
	TILE_ROOM_FLOOR(Solidity.WALK_ON),
	TILE_ROOM_DOOR(Solidity.WALK_THROUGH),

	TILE_CORRIDOR(Solidity.WALK_ON);

	private Solidity solidity;

	Tiles(Solidity solidity) {
		this.solidity = solidity;
	}

	public Solidity getSolidity() {
		return solidity;
	}
}
