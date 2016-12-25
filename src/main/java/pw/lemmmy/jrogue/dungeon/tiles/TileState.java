package pw.lemmmy.jrogue.dungeon.tiles;

import org.json.JSONObject;

public abstract class TileState {
	private Tile tile;

	public TileState(Tile tile) {
		this.tile = tile;
	}

	public Tile getTile() {
		return tile;
	}

	public void serialise(JSONObject obj) {}

	public void unserialise(JSONObject obj) {}
}
