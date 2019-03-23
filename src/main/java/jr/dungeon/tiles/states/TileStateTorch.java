package jr.dungeon.tiles.states;

import com.google.gson.annotations.Expose;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Tile;
import jr.utils.Colour;
import org.apache.commons.lang3.tuple.Pair;

@Registered(id="tileStateTorch")
public class TileStateTorch extends TileState {
	@Expose private Colour colour1 = new Colour(0xFF9B26FF);
	@Expose private Colour colour2 = new Colour(0xFF1F0CFF);
	
	public TileStateTorch(Tile tile) {
		super(tile);
	}
	
	@Override
	public Colour getLightColour() {
		return colour1;
	}
	
	@Override
	public int getLightIntensity() {
		return 100;
	}
	
	@Override
	public int getLightAbsorb() {
		return 0;
	}
	
	public Colour getParticleDarkColour() {
		return colour2;
	}
	
	public void setColours(Pair<Colour, Colour> colours) {
		colour1 = colours.getLeft();
		colour2 = colours.getRight();
	}
}
