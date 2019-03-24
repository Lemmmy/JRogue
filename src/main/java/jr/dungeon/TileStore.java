package jr.dungeon;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import jr.JRogue;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.events.TileChangedEvent;
import jr.dungeon.tiles.states.TileState;
import jr.utils.Point;
import jr.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

@Getter
public class TileStore implements LevelStore {
	private Level level;
	
	@Expose private Tile[] tiles;
	
	@Expose private int width;
	@Expose private int height;
	
	@Setter	private boolean eventsSuppressed = true; // start off suppressed during initialisation
	
	public TileStore(Level level) {
		this.level = level;
	}
	
	@Override
	public void initialise() {
		this.width = level.getWidth();
		this.height = level.getHeight();
		
		this.tiles = newTiles();
	}
	
	private Tile[] newTiles() {
		Tile[] tiles = new Tile[width * height];
		
		for (int i = 0; i < width * height; i++) {
			tiles[i] = new Tile(level, TileType.TILE_GROUND, i % width, (int) Math.floor(i / width));
		}
		
		return tiles;
	}
	
	@Override
	public void serialise(Gson gson, JsonObject out) {
		out.addProperty("tiles", new String(Base64.getEncoder().encode(serialiseTiles())));
		
		TypeAdapter<TileState> tileStateAdapter = gson.getAdapter(TypeToken.get(TileState.class));
		JsonArray tileStates = new JsonArray();
		Arrays.stream(tiles)
			.filter(Tile::hasState)
			.forEach(tile -> {
				JsonObject serialisedTileState = tileStateAdapter.toJsonTree(tile.getState()).getAsJsonObject();
				serialisedTileState.addProperty("x", tile.getX());
				serialisedTileState.addProperty("y", tile.getY());
				tileStates.add(serialisedTileState);
			});
		out.add("tileStates", tileStates);
	}
	
	private byte[] serialiseTiles() {
		try (
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos)
		) {
			for (Tile t : tiles) {
				dos.writeShort(t.getType().getID());
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
		eventsSuppressed = true;
		
		unserialiseTiles(Base64.getDecoder().decode(in.get("tiles").getAsString()));
		
		TypeAdapter<TileState> tileStateAdapter = gson.getAdapter(TypeToken.get(TileState.class));
		in.getAsJsonArray("tileStates").forEach(raw -> {
			JsonObject serialisedTileState = raw.getAsJsonObject();
			int x = serialisedTileState.get("x").getAsInt();
			int y = serialisedTileState.get("y").getAsInt();
			
			Tile tile = getTile(x, y);
			TileState tileState = tileStateAdapter.fromJsonTree(serialisedTileState);
			tile.setState(tileState);
		});
		
		eventsSuppressed = false;
	}
	
	private void unserialiseTiles(byte[] bytes) {
		Tile[] tiles = newTiles();
		
		try (
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis)
		) {
			for (Tile t : tiles) {
				short id = dis.readShort();
				TileType type = TileType.fromID(id);
				t.setType(type);
			}
		} catch (IOException e) {
			JRogue.getLogger().error("IO error loading level - during TileStore unserialiseTiles:", e);
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
		return tiles[y * width + x];
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
		tiles[y * width + x].setType(tile);
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