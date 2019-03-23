package jr.dungeon.tiles.states;

import com.google.gson.annotations.Expose;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import lombok.Getter;
import lombok.Setter;

@Registered(id="tileStateTrap")
public class TileStateTrap extends TileState {
	@Expose @Getter @Setter private TileType disguise;
	@Expose @Getter @Setter private boolean identified;

	public TileStateTrap(Tile tile) {
		super(tile);
		disguise = TileType.TILE_ROOM_RUG;
	}
}
