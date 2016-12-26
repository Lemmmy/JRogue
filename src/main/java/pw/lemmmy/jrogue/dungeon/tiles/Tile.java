package pw.lemmmy.jrogue.dungeon.tiles;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Level;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Tile {
	public int x;
	public int y;
	
	private TileType type;
	private TileState state;
	
	private Color light;
	private int lightIntensity;
	private int absorb;
	
	private Level level;
	
	public Tile(Level level, TileType type, int x, int y) {
		this.level = level;
		this.type = type;
		
		this.x = x;
		this.y = y;
		
		resetLight();
		
		initialiseState();
	}
	
	public void resetLight() {
		light = type.getLight();
		lightIntensity = type.getLightIntensity();
		absorb = type.getAbsorb();
		
		if (light == null) {
			light = level.getAmbientLight();
			lightIntensity = level.getAmbientLightIntensity();
		}
		
		light = level.applyIntensity(light, lightIntensity);
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
