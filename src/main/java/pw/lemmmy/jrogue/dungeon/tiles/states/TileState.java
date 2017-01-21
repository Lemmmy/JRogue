package pw.lemmmy.jrogue.dungeon.tiles.states;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.utils.Serialisable;

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
