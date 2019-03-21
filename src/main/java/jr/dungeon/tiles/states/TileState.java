package jr.dungeon.tiles.states;

import jr.dungeon.events.EventListener;
import jr.dungeon.tiles.Tile;
import jr.utils.Colour;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class TileState implements EventListener {
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
}
