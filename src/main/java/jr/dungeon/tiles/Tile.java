package jr.dungeon.tiles;

import com.badlogic.gdx.utils.Pool;
import jr.JRogue;
import jr.dungeon.Level;
import jr.dungeon.tiles.states.TileState;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Tile implements Pool.Poolable {
	private static final Pool<Tile> tilePool = new Pool<Tile>() {
		@Override
		protected Tile newObject() {
			return new Tile();
		}
	};

	public int x;
	public int y;
	
	private TileType type;
	private TileState state;
	
	private Color light;
	private int lightIntensity;
	private int absorb;
	
	private Level level;

	private Tile() {
		reset();
	}

	public static Tile getTile(Level level, TileType type, int x, int y) {
		Tile tile = tilePool.obtain();
		tile.initialise(level, type, x, y);
		return tile;
	}

	public static void free(Tile tile) {
		tilePool.free(tile);
	}

	@Override
	public void reset() {
		this.x = 0;
		this.y = 0;
		this.type = null;
		this.state = null;
		this.light = null;
		this.lightIntensity = 0;
		this.absorb = 0;
		this.level = null;
	}
	
	private void initialise(Level level, TileType type, int x, int y) {
		this.level = level;
		this.type = type;
		
		this.x = x;
		this.y = y;
		
		initialiseState();
	}
	
	public void resetLight() {
		light = type.getLight();
		lightIntensity = type.getLightIntensity();
		absorb = type.getAbsorb();
		
		if (light == null) {
			light = level.getLightStore().getAmbientLight();
			lightIntensity = level.getLightStore().getAmbientLightIntensity();
		}
		
		light = level.getLightStore().applyIntensity(light, lightIntensity);
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
	
	public TileType getType() {
		return type;
	}
	
	public void setType(TileType type) {
		if (type.getStateClass() != this.type.getStateClass()) {
			this.type = type;
			
			initialiseState();
		} else {
			this.type = type;
		}
	}
	
	public Level getLevel() {
		return level;
	}
	
	public TileState getState() {
		return state;
	}
	
	public boolean hasState() {
		return state != null;
	}
	
	public void setState(TileState state) {
		this.state = state;
	}
	
	public Color getLightColour() {
		return light;
	}
	
	public void setLightColour(Color light) {
		this.light = light;
	}
	
	public int getLightIntensity() {
		return lightIntensity;
	}
	
	public void setLightIntensity(int lightIntensity) {
		this.lightIntensity = lightIntensity;
	}
	
	public int getAbsorb() {
		return absorb;
	}
	
	public void setAbsorb(int absorb) {
		this.absorb = absorb;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Tile && ((Tile) o).getX() == x && ((Tile) o).getY() == y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
