package pw.lemmmy.jrogue.dungeon;

import java.awt.*;

public class Tile {
	public int x;
	public int y;
	private TileType type;
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
	}

	protected void resetLight() {
		light = type.getLight();
		lightIntensity = type.getLightIntensity();
		absorb = type.getAbsorb();

		if (light == null) {
			light = level.getAmbientLight();
			lightIntensity = level.getAmbientLightIntensity();
		}

		light = level.applyIntensity(light, lightIntensity);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public TileType getType() {
		return type;
	}

	public void setType(TileType type) {
		this.type = type;
	}

	public Color getLight() {
		return light;
	}

	public void setLight(Color light) {
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
}
