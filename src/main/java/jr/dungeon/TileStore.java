package jr.dungeon;

import jr.JRogue;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.events.TileChangedEvent;
import jr.dungeon.tiles.states.TileState;
import jr.utils.Point;
import jr.utils.Serialisable;
import jr.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Getter
public class TileStore implements Serialisable {
	private Level level;
	
	private Tile[] tiles;
	
	private int width;
	private int height;
	
	@Setter private boolean eventsSuppressed;
	
	public void initialise(Level level) {
		this.level = level;
		
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
					JRogue.getLogger().error("Error saving level:", e);
				}
			});
			
			dos.flush();
			
			return Optional.of(bos.toByteArray());
		} catch (IOException e) {
			JRogue.getLogger().error("Error saving level:", e);
		}
		
		return Optional.empty();
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		eventsSuppressed = true;
		
		try {
			unserialiseTiles(Base64.getDecoder().decode(obj.getString("tiles")));
			
			JSONArray serialisedTileStates = obj.getJSONArray("tileStates");
			serialisedTileStates.forEach(serialisedTileState -> unserialiseTileState((JSONObject) serialisedTileState));
		} catch (Exception e) {
			JRogue.getLogger().error("Error loading level - during TileStore unserialisation:", e);
		}
		
		eventsSuppressed = false;
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
					JRogue.getLogger().error("Error loading level - during TileStore unserialiseTiles:", e);
				}
			});
		} catch (IOException e) {
			JRogue.getLogger().error("IO error loading level - during TileStore unserialiseTiles:", e);
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
			JRogue.getLogger().error("Unknown tile state class {}", tileStateClassName, e);
		} catch (NoSuchMethodException e) {
			JRogue.getLogger().error("Tile state class {} has no unserialisation constructor", tileStateClassName, e);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			JRogue.getLogger().error("Error loading tile state class {}", tileStateClassName, e);
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
		if (x < 0 || y < 0 || x >= width || y >= height) return null;
		return tiles[width * y + x];
	}
	
	public Tile getTile(Point point) {
		return getTile(point.getX(), point.getY());
	}
	
	public TileType getTileType(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) return null;
		return getTile(x, y).getType();
	}
	
	public TileType getTileType(Point point) {
		return getTileType(point.getX(), point.getY());
	}
	
	public void setTileType(int x, int y, TileType tile) {
		if (tile.getID() < 0) return;
		if (x < 0 || y < 0 || x >= width || y >= height) return;
		tiles[width * y + x].setType(tile);
	}

	public void setTileType(Point p, TileType tile) {
		setTileType(p.getX(), p.getY(), tile);
	}
	
	public void triggerTileSetEvent(Tile tile, TileType oldType, TileType newType) {
		level.getDungeon().eventSystem.triggerEvent(new TileChangedEvent(tile, oldType, newType));
	}

	public Tile[] getAdjacentTiles(int x, int y) {
		return Arrays.stream(Utils.DIRECTIONS)
			.map(d -> getTile(x + d.getX(), y + d.getY()))
			.toArray(Tile[]::new);
	}

	public Tile[] getAdjacentTiles(Point p) {
		return getAdjacentTiles(p.getX(), p.getY());
	}

	public TileType[] getAdjacentTileTypes(int x, int y) {
		return Arrays.stream(Utils.DIRECTIONS)
			.map(d -> getTileType(x + d.getX(), y + d.getY()))
			.toArray(TileType[]::new);
	}

	public TileType[] getAdjacentTileTypes(Point p) {
		return getAdjacentTileTypes(p.getX(), p.getY());
	}
	
	public Tile[] getOctAdjacentTiles(int x, int y) {
		return Arrays.stream(Utils.OCT_DIRECTIONS)
			.map(d -> getTile(x + d.getX(), y + d.getY()))
			.toArray(Tile[]::new);
	}

	public Tile[] getOctAdjacentTiles(Point p) {
		return getOctAdjacentTiles(p.getX(), p.getY());
	}
	
	public TileType[] getOctAdjacentTileTypes(int x, int y) {
		return Arrays.stream(Utils.OCT_DIRECTIONS)
			.map(d -> getTileType(x + d.getX(), y + d.getY()))
			.toArray(TileType[]::new);
	}

	public TileType[] getOctAdjacentTileTypes(Point p) {
		return getOctAdjacentTileTypes(p.getX(), p.getY());
	}
	
	public List<Tile> getTilesInRadius(int x, int y, int r) {
		List<Tile> found = new LinkedList<>();
		
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

	public List<Tile> getTilesInRadius(Point p, int r) {
		return getTilesInRadius(p.getX(), p.getY(), r);
	}
}