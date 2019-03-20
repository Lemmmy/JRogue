package jr.dungeon.tiles;

import jr.JRogue;
import jr.debugger.utils.Debuggable;
import jr.dungeon.Level;
import jr.dungeon.tiles.states.TileState;
import jr.utils.Colour;
import jr.utils.Point;
import jr.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Getter
@Setter
public class Tile implements Debuggable {
	private int x;
	private int y;
	
	private TileType type;
	private TileState state;
	
	private Colour lightColour;
	private int lightIntensity;
	private int lightAbsorb;
	
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
		lightColour = state != null && state.getLightColour() != null ? state.getLightColour() : type.getLightColour();
		lightIntensity = state != null && state.getLightIntensity() != -1 ? state.getLightIntensity() : type.getLightIntensity();
		lightAbsorb = state != null && state.getLightAbsorb() != -1 ? state.getLightAbsorb() : type.getLightAbsorb();
		
		if (lightColour == null) {
			lightColour = level.lightStore.getAmbientLight();
			lightIntensity = level.lightStore.getAmbientLightIntensity();
		}
		
		lightColour = level.lightStore.applyIntensity(lightColour, lightIntensity).copy();
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
		TileType oldType = this.type;
		
		if (type.getStateClass() != this.type.getStateClass()) {
			this.type = type;
			
			initialiseState();
		} else {
			this.type = type;
		}
		
		level.tileStore.triggerTileSetEvent(this, oldType, type);
	}
	
	/**
	 * @param target The target to check adjacency to.
	 *
	 * @return Whether or not the tile is adjacent to the target - checks for a
	 * {@link Utils#chebyshevDistance(int, int, int, int) Chebyshev distance} of 1 or less.
	 */
	public boolean isAdjacentTo(Tile target) {
		return Utils.chebyshevDistance(x, y, target.getX(), target.getY()) <= 1;
	}
	
	/**
	 * @param target The target to check adjacency to.
	 *
	 * @return Whether or not the tile is adjacent to the target - checks for a
	 * {@link Utils#octileDistance(int, int, int, int, float, float)} Octile distance} of 1 or less.
	 */
	public boolean isAdjacentTo(Point target) {
		return Utils.octileDistance(x, y, target.getX(), target.getY(), 1, 1) <= 1;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Tile && ((Tile) o).getX() == x && ((Tile) o).getY() == y;
	}
	
	@Override
	public String getValueHint() {
		return String.format(
			"[P_GREY_3]%s[] %,d, %,d",
			type.name().replaceFirst("^TILE_", ""),
			x, y
		);
	}
}
