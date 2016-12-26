package pw.lemmmy.jrogue.dungeon.tiles;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Serialisable;

public abstract class TileState implements Serialisable {
	private Tile tile;

	public TileState(Tile tile) {
		this.tile = tile;
	}

	public Tile getTile() {
		return tile;
	}

	@Override
	public void serialise(JSONObject obj) {}

  @Override
	public void unserialise(JSONObject obj) {}
}
