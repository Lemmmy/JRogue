package jr.dungeon;

import jr.JRogue;
import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.states.TileState;
import jr.utils.Serialisable;
import jr.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

public class TileStore implements Serialisable, Closeable {
	private static final int LIGHT_MAX_LIGHT_LEVEL = 100;
	private static final int LIGHT_ABSOLUTE = 80;
	
	private Dungeon dungeon;
	private Level level;
	
	private Tile[] tiles;
	
	private Boolean[] discoveredTiles;
	private Boolean[] visibleTiles;
	private List<List<Tile>> lightTiles;
	
	private int width;
	private int height;
	
	public TileStore(Level level) {
		this.dungeon = level.getDungeon();
		this.level = level;
		
		this.width = level.getWidth();
		this.height = level.getHeight();
	}
	
	public void initialise() {
		tiles = new Tile[width * height];
		discoveredTiles = new Boolean[width * height];
		visibleTiles = new Boolean[width * height];
		
		for (int i = 0; i < width * height; i++) {
			tiles[i] = Tile.getTile(level, TileType.TILE_GROUND, i % width, (int) Math.floor(i / width));
		}
		
		Arrays.fill(discoveredTiles, false);
		Arrays.fill(visibleTiles, false);
	}
	
	@Override
	public void serialise(JSONObject obj) {
		serialiseTiles().ifPresent(bytes -> obj.put("tiles", new String(Base64.getEncoder().encode(bytes))));
		
		serialiseLights().ifPresent(bytes -> obj.put("lights", new String(Base64.getEncoder().encode(bytes))));
		
		serialiseBooleanArray(visibleTiles)
			.ifPresent(bytes -> obj.put("visibleTiles", new String(Base64.getEncoder().encode(bytes))));
		
		serialiseBooleanArray(discoveredTiles)
			.ifPresent(bytes -> obj.put("discoveredTiles", new String(Base64.getEncoder().encode(bytes))));
		
		Arrays.stream(tiles).forEach(t -> {
			if (t.hasState()) {
				JSONObject serialisedTileState = new JSONObject();
				serialisedTileState.put("x", t.getX());
				serialisedTileState.put("y", t.getY());
				serialisedTileState.put("class", t.getState().getClass().getName());
				t.getState().serialise(serialisedTileState);
				obj.append("tileStates", serialisedTileState);
			}
		});
	}
	
	private Optional<byte[]> serialiseTiles() {
		try (
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos)
		) {
			Arrays.stream(tiles).forEach(t -> {
				try {
					dos.writeShort(t.getType().getID());
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
	
	private Optional<byte[]> serialiseLights() {
		try (
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos)
		) {
			Arrays.stream(tiles).forEach(t -> {
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
	
	private Optional<byte[]> serialiseBooleanArray(Boolean[] arr) {
		try (
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos)
		) {
			Arrays.stream(arr).forEach(t -> {
				try {
					dos.writeBoolean(t);
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
		unserialiseTiles(Base64.getDecoder().decode(obj.getString("tiles")));
		
		unserialiseLights(Base64.getDecoder().decode(obj.getString("lights")));
		
		visibleTiles = unserialiseBooleanArray(
			Base64.getDecoder().decode(obj.getString("visibleTiles")),
			width * height
		);
		
		discoveredTiles = unserialiseBooleanArray(
			Base64.getDecoder().decode(obj.getString("discoveredTiles")),
			width * height
		);
		
		JSONArray serialisedTileStates = obj.getJSONArray("tileStates");
		serialisedTileStates.forEach(serialisedTileState -> unserialiseTileState((JSONObject) serialisedTileState));
	}
	
	private void unserialiseTiles(byte[] bytes) {
		try (
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis)
		) {
			Arrays.stream(tiles).forEach(t -> {
				try {
					short id = dis.readShort();
					TileType type = TileType.fromID(id);
					t.setType(type);
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
	
	private void unserialiseLights(byte[] bytes) {
		try (
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis)
		) {
			Arrays.stream(tiles).forEach(t -> {
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
	
	private Boolean[] unserialiseBooleanArray(byte[] bytes, int count) {
		Boolean[] out = new Boolean[count];
		
		try (
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis)
		) {
			for (int i = 0; i < count; i++) {
				out[i] = dis.readBoolean();
			}
		} catch (IOException e) {
			JRogue.getLogger().error("Error loading level:");
			JRogue.getLogger().error(e);
		}
		
		return out;
	}
	
	private void unserialiseTileState(JSONObject serialisedTileState) {
		String tileStateClassName = serialisedTileState.getString("class");
		int x = serialisedTileState.getInt("x");
		int y = serialisedTileState.getInt("y");
		
		Tile tile = getTile(x, y);
		
		try {
			@SuppressWarnings("unchecked")
			Class<? extends TileState> tileStateClass = (Class<? extends TileState>) Class.forName(tileStateClassName);
			Constructor<? extends TileState> tileStateConstructor = tileStateClass.getConstructor(Tile.class);
			
			TileState tileState = tileStateConstructor.newInstance(tile);
			tileState.unserialise(serialisedTileState);
			tile.setState(tileState);
		} catch (ClassNotFoundException e) {
			JRogue.getLogger().error("Unknown tile state class {}", tileStateClassName);
		} catch (NoSuchMethodException e) {
			JRogue.getLogger().error("Tile state class {} has no unserialisation constructor", tileStateClassName);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			JRogue.getLogger().error("Error loading tile state class {}", tileStateClassName);
			JRogue.getLogger().error(e);
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Tile[] getTiles() {
		return tiles;
	}
	
	public Boolean[] getDiscoveredTiles() {
		return discoveredTiles;
	}
	
	public Tile getTile(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return null;
		}
		
		return tiles[width * y + x];
	}
	
	public TileType getTileType(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return null;
		}
		
		return getTile(x, y).getType();
	}
	
	public void setTileType(int x, int y, TileType tile) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return;
		}
		
		tiles[width * y + x].setType(tile);
	}
	
	public Tile[] getAdjacentTiles(int x, int y) {
		Tile[] t = new Tile[Utils.DIRECTIONS.length];
		
		for (int i = 0; i < Utils.DIRECTIONS.length; i++) {
			int[] direction = Utils.DIRECTIONS[i];
			
			t[i] = getTile(x + direction[0], y + direction[1]);
		}
		
		return t;
	}
	
	public TileType[] getAdjacentTileTypes(int x, int y) {
		TileType[] t = new TileType[Utils.DIRECTIONS.length];
		
		for (int i = 0; i < Utils.DIRECTIONS.length; i++) {
			int[] direction = Utils.DIRECTIONS[i];
			
			t[i] = getTileType(x + direction[0], y + direction[1]);
		}
		
		return t;
	}
	
	public List<Tile> getTilesInRadius(int x, int y, int r) {
		List<Tile> found = new ArrayList<>();
		
		for (int j = y - r; j < y + r; j++) {
			for (int i = x - r; i < x + r; i++) {
				if (Utils.distance(x, y, i, j) <= r) {
					Tile t = getTile(i, j);
					
					if (t != null) {
						found.add(t);
					}
				}
			}
		}
		
		return found;
	}
	
	public boolean isTileDiscovered(int x, int y) {
		return !(x < 0 || y < 0 || x >= width || y >= height) && discoveredTiles[y * width + x];
	}
	
	public void discoverTile(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return;
		}
		
		discoveredTiles[width * y + x] = true;
	}
	
	public Boolean[] getVisibleTiles() {
		return visibleTiles;
	}
	
	public boolean isTileVisible(int x, int y) {
		return !isTileInvisible(x, y);
	}
	
	public boolean isTileInvisible(int x, int y) {
		return x < 0 || y < 0 || x >= width || y >= height || !visibleTiles[width * y + x];
	}
	
	public void seeTile(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return;
		}
		
		visibleTiles[y * width + x] = true;
		
		level.getEntityStore().getEntities().stream()
			.filter(e -> e.getX() == x && e.getY() == y)
			.forEach(e -> {
				e.setLastSeenX(x);
				e.setLastSeenY(y);
			});
	}
	
	public void updateSight(Player player) {
		Arrays.fill(visibleTiles, false);
		
		float x = player.getX() + 0.5f;
		float y = player.getY() + 0.5f;
		
		for (int r = 0; r < 360; r++) {
			int corridorVisibility = 0;
			boolean breakNext = false;
			
			for (int i = 0; i < player.getVisibilityRange(); i++) {
				double a = Math.toRadians(r);
				int dx = (int) Math.floor(x + i * Math.cos(a));
				int dy = (int) Math.floor(y + i * Math.sin(a));
				TileType type = getTileType(dx, dy);
				
				if (type == TileType.TILE_CORRIDOR) {
					corridorVisibility += 1;
				}
				
				if (corridorVisibility >= player.getCorridorVisibilityRange()) {
					break;
				}
				
				discoverTile(dx, dy);
				seeTile(dx, dy);
				
				if (dx < 0 || dy < 0 || dx >= width || dy >= height ||
					type.getSolidity() == TileType.Solidity.SOLID ||
					!(dx == player.getX() && dy == player.getY()) && type.isSemiTransparent() ||
					breakNext) {
					break;
				}
				
				if (dx == player.getX() && dy == player.getY() && type.isSemiTransparent()) {
					breakNext = true;
				}
			}
		}
	}
	
	public void seeAll() {
		Arrays.fill(visibleTiles, true);
		Arrays.fill(discoveredTiles, true);
	}
	
	@Override
	public void close() {
		for (Tile t : tiles) {
			Tile.free(t);
		}
	}
}