package jr.dungeon;

import jr.JRogue;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.Colour;
import jr.utils.Serialisable;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class LightStore implements Serialisable {
	private static final int LIGHT_MAX_LIGHT_LEVEL = 100;
	private static final int LIGHT_ABSOLUTE = 80;
	
	private Level level;
	
	private List<List<Tile>> lightTiles;
	
	private Colour workingColour = new Colour(0x000000FF);
	
	public LightStore(Level level) {
		this.level = level;
		
		lightTiles = new ArrayList<>();
	}
	
	@Override
	public void serialise(JSONObject obj) {
		serialiseLights().ifPresent(bytes -> obj.put("lights", new String(Base64.getEncoder().encode(bytes))));
	}
	
	private Optional<byte[]> serialiseLights() {
		try (
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos)
		) {
			Arrays.stream(level.getTileStore().getTiles()).forEach(t -> {
				try {
					dos.writeInt(Colour.rgba8888(t.getLightColour()));
					dos.writeByte(t.getLightIntensity());
				} catch (IOException e) {
					JRogue.getLogger().error("Error saving level:");
					JRogue.getLogger().error(e);
				}
			});
			
			dos.flush();
			
			return Optional.of(bos.toByteArray());
		} catch (IOException e) {
			JRogue.getLogger().error("Error saving level:");
			JRogue.getLogger().error(e);
		}
		
		return Optional.empty();
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		unserialiseLights(Base64.getDecoder().decode(obj.getString("lights")));
	}
	
	private void unserialiseLights(byte[] bytes) {
		try (
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis)
		) {
			Arrays.stream(level.getTileStore().getTiles()).forEach(t -> {
				try {
					int colourInt = dis.readInt();
					int intensity = dis.readByte();
					
					t.setLightColour(new Colour(colourInt));
					t.setLightIntensity(intensity);
				} catch (IOException e) {
					JRogue.getLogger().error("Error loading level:");
					JRogue.getLogger().error(e);
				}
			});
		} catch (IOException e) {
			JRogue.getLogger().error("Error loading level:");
			JRogue.getLogger().error(e);
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
		
		for (Tile tile : level.getTileStore().getTiles()) {
			int index = tile.getLightIntensity() - 1;
			
			if (index < 0) { continue; }
			if (index >= LIGHT_MAX_LIGHT_LEVEL) { continue; }
			
			lightTiles.get(index).add(tile);
		}
		
		Stream.concat(level.getEntityStore().getEntities().stream(), level.getEntityStore().getEntityAddQueue().stream())
			.filter(e -> e instanceof LightEmitter)
			.forEach(e -> {
				LightEmitter lightEmitter = (LightEmitter) e;
				int index = lightEmitter.getLightIntensity() - 1;
				
				if (index < 0 || index >= LIGHT_MAX_LIGHT_LEVEL) { return; }
				
				Tile tile = new Tile(level, TileType.TILE_DUMMY, e.getX(), e.getY());
				
				if (!level.getVisibilityStore().isTileInvisible(tile.getX(), tile.getY()) && !isInitial) {
					tile.setLightColour(lightEmitter.getLightColour().copy());
					tile.setLightIntensity(lightEmitter.getLightIntensity());
				}
				
				lightTiles.get(index).add(tile);
			});
		
		for (int i = LIGHT_MAX_LIGHT_LEVEL - 1; i >= 0; i--) {
			java.util.List<Tile> lights = lightTiles.get(i);
			
			//noinspection ForLoopReplaceableByForEach because it's not
			for (int j = 0; j < lights.size(); j++) {
				Tile tile = lights.get(j);
				
				if (tile.getLightIntensity() != i + 1) { continue; }
				
				propagateLighting(tile, isInitial);
			}
		}
	}
	
	public void initialiseLight() {
		lightTiles = new ArrayList<>();
		
		for (int i = 0; i < LIGHT_MAX_LIGHT_LEVEL; i++) {
			lightTiles.add(i, new ArrayList<>());
		}
		
		Arrays.stream(level.getTileStore().getTiles())
			.forEach(Tile::resetLight);
	}
	
	public void resetLight() {
		lightTiles = new ArrayList<>();
		
		for (int i = 0; i < LIGHT_MAX_LIGHT_LEVEL; i++) {
			lightTiles.add(i, new ArrayList<>());
		}
		
		Arrays.stream(level.getTileStore().getTiles())
			.filter(t -> !level.getVisibilityStore().isTileInvisible(t.getX(), t.getY()))
			.forEach(Tile::resetLight);
	}
	
	public void propagateLighting(Tile tile, boolean isInitial) {
		int x = tile.getX();
		int y = tile.getY();
		
		int intensity = tile.getLightIntensity() - tile.getAbsorb();
		
		if (intensity < 0) {
			return;
		}
		
		workingColour.set(tile.getLightColour());
		reapplyIntensity(workingColour, tile.getLightIntensity(), intensity);
		
		if (x > 0) { setIntensity(level.getTileStore().getTile(x - 1, y), intensity, workingColour, isInitial); }
		if (x < level.getWidth() - 1) { setIntensity(level.getTileStore().getTile(x + 1, y), intensity, workingColour, isInitial); }
		if (y > 0) { setIntensity(level.getTileStore().getTile(x, y - 1), intensity, workingColour, isInitial); }
		if (y < level.getHeight() - 1) { setIntensity(level.getTileStore().getTile(x, y + 1), intensity, workingColour, isInitial); }
		
		workingColour.set(
			workingColour.r * 0.9f,
			workingColour.g * 0.9f,
			workingColour.b * 0.9f,
			workingColour.a
		);
		
		if (x > 0 && y < level.getWidth() - 1) { setIntensity(level.getTileStore().getTile(x - 1, y + 1), intensity, workingColour, isInitial); }
		if (x < level.getWidth() - 1 && y > 0) { setIntensity(level.getTileStore().getTile(x + 1, y - 1), intensity, workingColour, isInitial); }
		if (x > 0 && y < 0) { setIntensity(level.getTileStore().getTile(x - 1, y - 1), intensity, workingColour, isInitial); }
		if (x < level.getWidth() - 1 && y < level.getHeight() - 1) { setIntensity(level.getTileStore().getTile(x + 1, y + 1), intensity, workingColour, isInitial);}
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
		if (tile == null || level.getVisibilityStore().isTileInvisible(tile.getX(), tile.getY()) && !isInitial) {
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
