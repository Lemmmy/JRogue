package jr.dungeon.tiles.states;

import jr.dungeon.tiles.Tile;
import jr.utils.Colour;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

@Getter
public class TileStateTorch extends TileState {
	private Colour colour1 = new Colour(0xFF9B26FF);
	private Colour colour2 = new Colour(0xFF1F0CFF);
	
	public TileStateTorch(Tile tile) {
		super(tile);
	}
	
	public Colour getParticleDarkColour() {
		return colour2;
	}
	
	public void setColours(Pair<Colour, Colour> colours) {
		colour1 = colours.getLeft();
		colour2 = colours.getRight();
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("colour1", colour1.toIntBits());
		obj.put("colour2", colour2.toIntBits());
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		colour1 = obj.has("colour1") ? new Colour(obj.getInt("colour1")) : new Colour(0xFF9B26FF);
		colour2 = obj.has("colour2") ? new Colour(obj.getInt("colour2")) : new Colour(0xFF1F0CFF);
	}
}
