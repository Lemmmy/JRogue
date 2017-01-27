package jr.dungeon.tiles.states;

import jr.dungeon.tiles.Tile;
import jr.utils.Serialisable;
import org.json.JSONObject;

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
