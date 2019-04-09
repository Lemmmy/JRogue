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
import jr.utils.Directions;
import jr.utils.Distance;
import jr.utils.Point;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import static jr.utils.QuickMaths.ifloor;

@Getter
public class TileStore implements LevelStore {
    private Level level;
    
    @Expose private Tile[] tiles;
    
    @Expose private int width;
    @Expose private int height;
    
    @Setter    private boolean eventsSuppressed = true; // start off suppressed during initialisation
    
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
            tiles[i] = new Tile(level, TileType.TILE_GROUND, i % width, ifloor(i / width));
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
                serialisedTileState.addProperty("x", tile.position.x);
                serialisedTileState.addProperty("y", tile.position.y);
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
        
        deserialiseTiles(Base64.getDecoder().decode(in.get("tiles").getAsString()));
        
        TypeAdapter<TileState> tileStateAdapter = gson.getAdapter(TypeToken.get(TileState.class));
        in.getAsJsonArray("tileStates").forEach(raw -> {
            JsonObject serialisedTileState = raw.getAsJsonObject();
            int x = serialisedTileState.get("x").getAsInt();
            int y = serialisedTileState.get("y").getAsInt();
            
            Tile tile = getTile(Point.get(x, y));
            TileState tileState = tileStateAdapter.fromJsonTree(serialisedTileState);
            tileState.init(tile);
            tile.setState(tileState);
        });
        
        eventsSuppressed = false;
    }
    
    private void deserialiseTiles(byte[] bytes) {
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
            JRogue.getLogger().error("IO error loading level - during TileStore deserialiseTiles:", e);
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
    
    public Tile getTile(Point point) {
        if (!point.insideLevel(level)) return null;
        return tiles[point.getIndex(level)];
    }
    
    /**
     * Raw tile getter, only for extremely hot loops. Use {@link Point} where possible.
     */
    public Tile getTileRaw(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) return null;
        return tiles[y * width + x];
    }
    
    public TileType getTileType(Point point) {
        if (!point.insideLevel(level)) return null;
        return getTile(point).getType();
    }
    
    /**
     * Raw tile type getter, only for extremely hot loops. Use {@link Point} where possible.
     */
    public TileType getTileTypeRaw(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) return null;
        return tiles[y * width + x].getType();
    }
    
    public Tile setTileType(Point point, TileType tile) {
        if (tile == null || tile.getID() < 0 || !point.insideLevel(level)) return null;
        return tiles[point.getIndex(level)].setType(tile);
    }
    
    public void triggerTileSetEvent(Tile tile, TileType oldType, TileType newType) {
        if (eventsSuppressed) return;
        level.getDungeon().eventSystem.triggerEvent(new TileChangedEvent(tile, oldType, newType));
    }

    public Tile[] getAdjacentTiles(Point point) {
        return Directions.cardinal()
            .map(point::add)
            .map(this::getTile)
            .toArray(Tile[]::new);
    }

    public TileType[] getAdjacentTileTypes(Point point) {
        return Directions.cardinal()
            .map(point::add)
            .map(this::getTileType)
            .toArray(TileType[]::new);
    }
    
    public Tile[] getOctAdjacentTiles(Point point) {
        return Directions.compass()
            .map(point::add)
            .map(this::getTile)
            .toArray(Tile[]::new);
    }
    
    public TileType[] getOctAdjacentTileTypes(Point point) {
        return Directions.compass()
            .map(point::add)
            .map(this::getTileType)
            .toArray(TileType[]::new);
    }
    
    public List<Tile> getTilesInRadius(Point point, int r) {
        List<Tile> found = new LinkedList<>();
        
        int x = point.x; int y = point.y;
        
        for (int j = y - r; j < y + r; j++) {
            for (int i = x - r; i < x + r; i++) {
                if (i > 0 && j > 0 && i < level.getWidth() && j < level.getHeight() && Distance.i(x, y, i, j) <= r) {
                    found.add(tiles[j * level.getWidth() + i]);
                }
            }
        }
        
        return found;
    }
}