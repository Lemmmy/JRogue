package jr.dungeon;

import jr.JRogue;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.Serialisable;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class LightStore implements Serialisable {
	private static final int LIGHT_MAX_LIGHT_LEVEL = 100;
	private static final int LIGHT_ABSOLUTE = 80;
	
	private Dungeon dungeon;
	private Level level;
	
	private List<List<Tile>> lightTiles;
	
	public LightStore(Level level) {
		this.dungeon = level.getDungeon();
		this.level = level;
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
					dos.writeInt(t.getLightColour().getRGB());
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
					t.setLightColour(new Color(colourInt));
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
	
	public Color getAmbientLight() {
		return Color.WHITE;
	}
	
	public int getAmbientLightIntensity() {
		return 20;
	}
	
	public Color applyIntensity(Color colour, int intensity) {
		float k;
		
		k = intensity >= LIGHT_ABSOLUTE ? 1 : (float) intensity / (float) LIGHT_ABSOLUTE;
		
		return new Color(
			(int) (colour.getRed() * k),
			(int) (colour.getGreen() * k),
			(int) (colour.getBlue() * k),
			255
		);
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
				
				Tile tile = Tile.getTile(level, TileType.TILE_DUMMY, e.getX(), e.getY());
				
				if (!level.getTileStore().isTileInvisible(tile.getX(), tile.getY()) && !isInitial) {
					tile.setLightColour(lightEmitter.getLightColour());
					tile.setLightIntensity(lightEmitter.getLightIntensity());
				}
				
				lightTiles.get(index).add(tile);
			});
		
		for (int i = LIGHT_MAX_LIGHT_LEVEL - 1; i >= 0; i--) {
			java.util.List<Tile> lights = lightTiles.get(i);
			
			//noinspection ForLoopReplaceableByForEach
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
		
		Arrays.stream(level.getTileStore().getTiles())
			.filter(t -> !level.getTileStore().isTileInvisible(t.getX(), t.getY()))
			.forEach(Tile::resetLight);
	}
	
	public void propagateLighting(Tile tile, boolean isInitial) {
		int x = tile.getX();
		int y = tile.getY();
		
		int intensity = tile.getLightIntensity() - tile.getAbsorb();
		
		if (intensity < 0) {
			return;
		}
		
		Color colour = reapplyIntensity(tile.getLightColour(), tile.getLightIntensity(), intensity);
		
		if (x > 0) { setIntensity(level.getTileStore().getTile(x - 1, y), intensity, colour, isInitial); }
		if (x < level.getWidth() - 1) { setIntensity(level.getTileStore().getTile(x + 1, y), intensity, colour, isInitial); }
		if (y > 0) { setIntensity(level.getTileStore().getTile(x, y - 1), intensity, colour, isInitial); }
		if (y < level.getHeight() - 1) { setIntensity(level.getTileStore().getTile(x, y + 1), intensity, colour, isInitial); }
		
		colour = new Color(
			(int) (colour.getRed() * 0.9f),
			(int) (colour.getGreen() * 0.9f),
			(int) (colour.getBlue() * 0.9f),
			colour.getAlpha()
		);
		
		if (x > 0 && y < level.getWidth() - 1) { setIntensity(level.getTileStore().getTile(x - 1, y + 1), intensity, colour, isInitial); }
		if (x < level.getWidth() - 1 && y > 0) { setIntensity(level.getTileStore().getTile(x + 1, y - 1), intensity, colour, isInitial); }
		if (x > 0 && y < 0) { setIntensity(level.getTileStore().getTile(x - 1, y - 1), intensity, colour, isInitial); }
		if (x < level.getWidth() - 1 && y < level.getHeight() - 1) { setIntensity(level.getTileStore().getTile(x + 1, y + 1), intensity, colour, isInitial);}
	}
	
	public Color reapplyIntensity(Color colour, int intensityOld, int intensityNew) {
		float k1, k2;
		
		k1 = intensityNew >= LIGHT_ABSOLUTE ? 1 : (float) intensityNew / (float) LIGHT_ABSOLUTE;
		k2 = intensityOld >= LIGHT_ABSOLUTE ? 1 : (float) intensityOld / (float) LIGHT_ABSOLUTE;
		
		return new Color(
			(int) Math.min(255, colour.getRed() * k1 / k2),
			(int) Math.min(255, colour.getGreen() * k1 / k2),
			(int) Math.min(255, colour.getBlue() * k1 / k2),
			255
		);
	}
	
	public void setIntensity(Tile tile, int intensity, Color colour, boolean isInitial) {
		if (tile == null || level.getTileStore().isTileInvisible(tile.getX(), tile.getY()) && !isInitial) {
			return;
		}
		
		if (intensity > tile.getLightIntensity() || canMixColours(tile.getLightColour(), colour)) {
			tile.setLightColour(mixColours(tile.getLightColour(), colour));
			
			if (intensity != tile.getLightIntensity()) {
				tile.setLightIntensity(intensity);
			}
			
			int index = tile.getLightIntensity() - 1;
			
			if (index < 0) { return; }
			if (index >= LIGHT_MAX_LIGHT_LEVEL) { return; }
			
			lightTiles.get(index).add(tile);
		}
	}
	
	public boolean canMixColours(Color base, Color light) {
		return light.getRed() > base.getRed() ||
			light.getGreen() > base.getGreen() ||
			light.getBlue() > base.getBlue();
	}
	
	public Color mixColours(Color c1, Color c2) {
		return new Color(
			c1.getRed() > c2.getRed() ? c1.getRed() : c2.getRed(),
			c1.getGreen() > c2.getGreen() ? c1.getGreen() : c2.getGreen(),
			c1.getBlue() > c2.getBlue() ? c1.getBlue() : c2.getBlue(),
			255
		);
	}
}
