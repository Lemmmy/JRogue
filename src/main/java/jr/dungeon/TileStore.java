package jr.dungeon;

import jr.JRogue;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.states.TileState;
import jr.utils.Serialisable;
import jr.utils.Utils;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Getter
public class TileStore implements Serialisable {
	private Tile[] tiles;
	
	private int width;
	private int height;
	
	public TileStore(Level level) {
		this.width = level.getWidth();
		this.height = level.getHeight();
		
		tiles = new Tile[width * height];
		
		for (int i = 0; i < width * height; i++) {
			tiles[i] = new Tile(level, TileType.TILE_GROUND, i % width, (int) Math.floor(i / width));
		}
	}
	
	@Override
	public void serialise(JSONObject obj) {
		serialiseTiles().ifPresent(bytes -> obj.put("tiles", new String(Base64.getEncoder().encode(bytes))));
		
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
	
	@Override
	public void unserialise(JSONObject obj) {
		unserialiseTiles(Base64.getDecoder().decode(obj.getString("tiles")));
		
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
}