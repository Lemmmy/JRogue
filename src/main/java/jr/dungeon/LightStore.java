package jr.dungeon;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jr.JRogue;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.Colour;
import jr.utils.Directions;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

public class LightStore implements LevelStore {
	private static final int LIGHT_MAX_LIGHT_LEVEL = 100;
	private static final int LIGHT_ABSOLUTE = 80;
	
	private Level level;
	
	private List<List<Tile>> lightTiles;
	
	private Colour workingColour = new Colour(0x000000FF);
	
	public LightStore(Level level) {
		this.level = level;
	}
	
	public void initialise() {
		lightTiles = new ArrayList<>();
		
		for (int i = 0; i < LIGHT_MAX_LIGHT_LEVEL; i++) {
			lightTiles.add(i, new ArrayList<>());
		}
		
		Arrays.stream(level.tileStore.getTiles())
			.forEach(Tile::resetLight);
	}
	
	@Override
	public void serialise(Gson gson, JsonObject out) {
		out.addProperty("lights", new String(Base64.getEncoder().encode(serialiseLights())));
	}
	
	private byte[] serialiseLights() {
		try (
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos)
		) {
			for (Tile t : level.tileStore.getTiles()) {
				dos.writeInt(Colour.rgba8888(t.getLightColour()));
				dos.writeByte(t.getLightIntensity());
			}
			
			dos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			JRogue.getLogger().error("Error saving level:", e);
		}
		
		return new byte[] {};
	}
	
	@Override
	public void deserialise(Gson gson, JsonObject in) {
		deserialiseLights(Base64.getDecoder().decode(in.get("lights").getAsString()));
	}
	
	private void deserialiseLights(byte[] bytes) {
		try (
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis)
		) {
			for (Tile t : level.tileStore.getTiles()) {
				int colourInt = dis.readInt();
				int intensity = dis.readByte();
				
				t.setLightColour(new Colour(colourInt));
				t.setLightIntensity(intensity);
			}
		} catch (IOException e) {
			JRogue.getLogger().error("Error loading level:", e);
		}
	}
	
	public Colour getAmbientLight() {
		return Colour.WHITE;
	}
	
	public int getAmbientLightIntensity() {
		return 20;
	}
	
	public Colour applyIntensity(Colour colour, int intensity) {
		float k;
		
		k = intensity >= LIGHT_ABSOLUTE ? 1 : (float) intensity / (float) LIGHT_ABSOLUTE;
		
		workingColour.set(
			(int) (colour.r * 255 * k),
			(int) (colour.g * 255 * k),
			(int) (colour.b * 255 * k),
			255
		);
		
		return workingColour;
	}
	
	public void buildLight(boolean isInitial) {
		resetLight();
		
		for (Tile tile : level.tileStore.getTiles()) {
			int index = tile.getLightIntensity() - 1;
			
			if (index < 0) { continue; }
			if (index >= LIGHT_MAX_LIGHT_LEVEL) { continue; }
			
			lightTiles.get(index).add(tile);
		}
		
		Stream.concat(level.entityStore.getEntities().stream(), level.entityStore.getEntityAddQueue().stream())
			.filter(e -> e instanceof LightEmitter)
			.forEach(e -> {
				LightEmitter lightEmitter = (LightEmitter) e;
				int index = lightEmitter.getLightIntensity() - 1;
				
				if (index < 0 || index >= LIGHT_MAX_LIGHT_LEVEL) { return; }
				
				Tile tile = new Tile(level, TileType.TILE_DUMMY, e.getPosition());
				
				if (!level.visibilityStore.isTileInvisible(tile.position) && !isInitial) {
					tile.setLightColour(lightEmitter.getLightColour().copy());
					tile.setLightIntensity(lightEmitter.getLightIntensity());
				}
				
				lightTiles.get(index).add(tile);
			});
		
		for (int i = LIGHT_MAX_LIGHT_LEVEL - 1; i >= 0; i--) {
			List<Tile> lights = lightTiles.get(i);
			
			//noinspection ForLoopReplaceableByForEach because it's not
			for (int j = 0; j < lights.size(); j++) {
				Tile tile = lights.get(j);
				
				if (tile.getLightIntensity() != i + 1) { continue; }
				
				propagateLighting(tile, isInitial);
			}
		}
	}
	
	public void resetLight() {
		lightTiles = new ArrayList<>();
		
		for (int i = 0; i < LIGHT_MAX_LIGHT_LEVEL; i++) {
			lightTiles.add(i, new ArrayList<>());
		}
		
		Arrays.stream(level.tileStore.getTiles())
			.filter(t -> !level.visibilityStore.isTileInvisible(t.position))
			.forEach(Tile::resetLight);
	}
	
	public void propagateLighting(Tile tile, boolean isInitial) {
		int intensity = tile.getLightIntensity() - tile.getLightAbsorb();
		
		if (intensity < 0) {
			return;
		}
		
		workingColour.set(tile.getLightColour());
		reapplyIntensity(workingColour, tile.getLightIntensity(), intensity);
		
		propagate(tile, level, intensity, workingColour, isInitial);
		
		workingColour.set(
			workingColour.r * 0.9f,
			workingColour.g * 0.9f,
			workingColour.b * 0.9f,
			workingColour.a
		);
		
		propagate(tile, level, intensity, workingColour, isInitial);
	}
	
	private void propagate(Tile tile, Level level, int intensity, Colour workingColour, boolean isInitial) {
		Directions.cardinal()
			.map(tile.position::add)
			.filter(p -> p.insideLevel(level))
			.forEach(p -> setIntensity(level.tileStore.getTile(p), intensity, workingColour, isInitial));
	}
	
	public void reapplyIntensity(Colour colour, int intensityOld, int intensityNew) {
		float k1, k2;
		
		k1 = intensityNew >= LIGHT_ABSOLUTE ? 1 : (float) intensityNew / (float) LIGHT_ABSOLUTE;
		k2 = intensityOld >= LIGHT_ABSOLUTE ? 1 : (float) intensityOld / (float) LIGHT_ABSOLUTE;
		
		colour.set(
			(int) Math.min(255, colour.r * 255 * k1 / k2),
			(int) Math.min(255, colour.g * 255 * k1 / k2),
			(int) Math.min(255, colour.b * 255 * k1 / k2),
			255
		);
	}
	
	public void setIntensity(Tile tile, int intensity, Colour colour, boolean isInitial) {
		if (tile == null || level.visibilityStore.isTileInvisible(tile.position) && !isInitial) {
			return;
		}
		
		if (intensity > tile.getLightIntensity() || canMixColours(tile.getLightColour(), colour)) {
			mixColours(tile.getLightColour(), colour);
			tile.setLightColour(colour.copy());
			
			if (intensity != tile.getLightIntensity()) {
				tile.setLightIntensity(intensity);
			}
			
			int index = tile.getLightIntensity() - 1;
			
			if (index < 0) { return; }
			if (index >= LIGHT_MAX_LIGHT_LEVEL) { return; }
			
			lightTiles.get(index).add(tile);
		}
	}
	
	public boolean canMixColours(Colour base, Colour light) {
		return light.r > base.r ||
			   light.g > base.g ||
			   light.b > base.b;
	}
	
	public void mixColours(Colour c1, Colour c2) {
		workingColour.set(
			c1.r > c2.r ? c1.r : c2.r,
			c1.g > c2.g ? c1.g : c2.g,
			c1.b > c2.b ? c1.b : c2.b,
			1f
		);
	}
}
