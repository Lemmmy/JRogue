package jr.dungeon.tiles.states;

import jr.dungeon.events.EventListener;
import jr.dungeon.tiles.Tile;
import jr.utils.Colour;
import jr.utils.Serialisable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

@Getter
@AllArgsConstructor
public abstract class TileState implements Serialisable, EventListener {
	private Tile tile;
	
	public Colour getLightColour() {
		return null;
	}
	
	public int getLightIntensity() {
		return -1;
	}
	
	public int getLightAbsorb() {
		return -1;
	}
	
	@Override
	public void serialise(JSONObject obj) {}
	
	@Override
	public void unserialise(JSONObject obj) {}
}
