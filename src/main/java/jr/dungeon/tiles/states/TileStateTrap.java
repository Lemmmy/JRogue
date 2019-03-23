package jr.dungeon.tiles.states;

import com.google.gson.annotations.Expose;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

@Registered(id="tileStateTrap")
public class TileStateTrap extends TileState {
	@Getter @Setter @Expose private TileType disguise;
	@Getter @Setter @Expose private boolean identified;

	public TileStateTrap(Tile tile) {
		super(tile);
		disguise = TileType.TILE_ROOM_RUG;
	}

	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		obj.put("disguise", (int) disguise.getID());
	}

	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		disguise = TileType.fromID((short) obj.optInt("disguise", TileType.TILE_DEBUG_A.getID()));
	}
}
