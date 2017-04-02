package jr.dungeon.tiles;

import jr.JRogue;
import jr.dungeon.Level;
import jr.dungeon.tiles.states.TileState;
import jr.utils.Colour;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Getter
@Setter
public class Tile {
	private int x;
	private int y;
	
	private TileType type;
	private TileState state;
	
	private Colour lightColour;
	private int lightIntensity;
	private int absorb;
	
	private Level level;

	public Tile(Level level, TileType type, int x, int y) {
		this.level = level;
		this.type = type;
		
		this.x = x;
		this.y = y;
		
		initialiseState();
	}
	
	public jr.utils.Point getPosition() {
		return jr.utils.Point.getPoint(x, y);
	}
	
	public void resetLight() {
		lightColour = type.getLightColour();
		lightIntensity = type.getLightIntensity();
		absorb = type.getAbsorb();
		
		if (lightColour == null) {
			lightColour = level.getLightStore().getAmbientLight();
			lightIntensity = level.getLightStore().getAmbientLightIntensity();
		}
		
		lightColour = level.getLightStore().applyIntensity(lightColour, lightIntensity).copy();
	}
	
	private void initialiseState() {
		if (type.getStateClass() != null) {
			try {
				Class<? extends TileState> stateClass = type.getStateClass();
				Constructor<? extends TileState> stateConstructor = stateClass.getConstructor(Tile.class);
				
				state = stateConstructor.newInstance(this);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				JRogue.getLogger().error("Error initialising tile state", e);
			}
		}
	}
	
	public boolean hasState() {
		return state != null;
	}
	
	public void setType(TileType type) {
		if (type.getStateClass() != this.type.getStateClass()) {
			this.type = type;
			
			initialiseState();
		} else {
			this.type = type;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Tile && ((Tile) o).getX() == x && ((Tile) o).getY() == y;
	}
}
