package jr.dungeon.tiles;

import com.google.gson.annotations.Expose;
import jr.JRogue;
import jr.debugger.utils.Debuggable;
import jr.dungeon.Level;
import jr.dungeon.tiles.states.TileState;
import jr.utils.Colour;
import jr.utils.Distance;
import jr.utils.Point;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Getter
@Setter
public class Tile implements Debuggable {
	@Getter(AccessLevel.NONE)
	public final Point position;
	
	@Expose private TileType type;
	@Expose private TileState state;
	
	@Expose private Colour lightColour;
	@Expose private int lightIntensity;
	@Expose private int lightAbsorb;
	
	private Level level;

	public Tile(Level level, TileType type, Point position) {
		this.level = level;
		this.type = type;
		
		this.position = position;
		
		initialiseState();
	}
	
	public Tile(Level level, TileType type, int x, int y) {
		this(level, type, Point.get(x, y));
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
	
	public Tile setType(TileType type) {
		TileType oldType = this.type;
		this.type = type;
		
		if (type.getStateClass() == null) {
			state = null;
		} else if (!type.getStateClass().equals(oldType.getStateClass())) {
			initialiseState();
		}
		
		level.tileStore.triggerTileSetEvent(this, oldType, type);
		
		return this;
	}
	
	/**
	 * @param target The target to check adjacency to.
	 *
	 * @return Whether or not the tile is adjacent to the target - checks for a
	 * {@link Distance#chebyshev(int, int, int, int) Chebyshev distance} of 1 or less.
	 */
	public boolean isAdjacentTo(Tile target) {
		return Distance.chebyshev(position, target.position) <= 1;
	}
	
	/**
	 * @param target The target to check adjacency to.
	 *
	 * @return Whether or not the tile is adjacent to the target - checks for a
	 * {@link Distance#octile(int, int, int, int, float, float)} Octile distance} of 1 or less.
	 */
	public boolean isAdjacentTo(Point target) {
		return Distance.octile(position, target, 1, 1) <= 1;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Tile && ((Tile) o).position.equals(position) && ((Tile) o).level == level;
	}
	
	@Override
	public String getValueString() {
		return String.format(
			"[P_GREY_3]%s[] %s",
			type.name().replaceFirst("^TILE_", ""),
			position
		);
	}
}
