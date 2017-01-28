package jr.dungeon.tiles.states;

import jr.dungeon.tiles.Tile;
import jr.utils.Serialisable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

@Getter
@AllArgsConstructor
public abstract class TileState implements Serialisable {
	private Tile tile;
	
	@Override
	public void serialise(JSONObject obj) {}
	
	@Override
	public void unserialise(JSONObject obj) {}
}
