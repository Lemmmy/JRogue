package jr.dungeon.tiles.states;

import jr.dungeon.events.EventListener;
import jr.dungeon.serialisation.HasRegistry;
import jr.dungeon.serialisation.Serialisable;
import jr.dungeon.tiles.Tile;
import jr.utils.Colour;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@HasRegistry
public abstract class TileState implements Serialisable, EventListener {
	private Tile tile;
	
	public void init(Tile tile) {
		this.tile = tile;
	}
	
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
