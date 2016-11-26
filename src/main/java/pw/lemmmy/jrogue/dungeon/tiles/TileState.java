package pw.lemmmy.jrogue.dungeon.tiles;

public abstract class TileState {
	private Tile tile;

	public TileState(Tile tile) {
		this.tile = tile;
	}

	public Tile getTile() {
		return tile;
	}
}
