package pw.lemmmy.jrogue.dungeon.generators;

import com.github.alexeyr.pcg.Pcg32;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.generators.rooms.Room;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.Utils;
import pw.lemmmy.jrogue.utils.WeightedCollection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class DungeonGenerator {
	private static final WeightedCollection<TileType> DOOR_TYPES = new WeightedCollection<>();
	
	static {
		DOOR_TYPES.add(3, TileType.TILE_ROOM_DOOR_LOCKED);
		DOOR_TYPES.add(4, TileType.TILE_ROOM_DOOR_CLOSED);
		DOOR_TYPES.add(6, TileType.TILE_ROOM_DOOR_OPEN);
	}
	
	protected List<Room> rooms = new ArrayList<>();
	
	protected Level level;
	protected Optional<Tile> sourceTile;
	
	protected Pcg32 rand = new Pcg32();
	protected Random jrand = new Random();
	
	public DungeonGenerator(Level level, Optional<Tile> sourceTile) {
		this.level = level;
		this.sourceTile = sourceTile;
	}
	
	public abstract boolean generate();
	
	protected int nextInt(int min, int max) {
		return rand.nextInt(max - min) + min;
	}
	
	public abstract TileType getTorchTileType();
	
	protected boolean canBuildRoom(int roomX, int roomY, int roomWidth, int roomHeight) {
		// the offsets are to prevent rooms directly touching each other
		
		for (int y = roomY - 2; y < roomY + roomHeight + 2; y++) {
			for (int x = roomX - 2; x < roomX + roomWidth + 2; x++) {
				if (level.getTileType(x, y) == null || !level.getTileType(x, y).isBuildable()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	protected Room buildRoom(Class<? extends Room> roomType, int roomX, int roomY, int roomWidth, int roomHeight) {
		try {
			Constructor<? extends Room> roomConstructor = roomType.getConstructor(
				Level.class, int.class, int.class, int.class, int.class
			);
			
			Room room = roomConstructor.newInstance(level, roomX, roomY, roomWidth, roomHeight);
			room.build(this);
			
			rooms.add(room);
			return room;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			JRogue.getLogger().error("Error building rooms", e);
		}
		
		return null;
	}
	
	protected void buildLine(int startX,
							 int startY,
							 int endX,
							 int endY,
							 TileType tile,
							 boolean buildableCheck,
							 boolean buildDoors) {
		float diffX = endX - startX;
		float diffY = endY - startY;
		
		float dist = Math.abs(diffX) + Math.abs(diffY);
		
		float dx = diffX / dist;
		float dy = diffY / dist;
		
		for (int i = 0; i <= Math.ceil(dist); i++) {
			int x = Math.round(startX + dx * i);
			int y = Math.round(startY + dy * i);
			
			if (level.getTileType(x, y).isBuildable()) {
				level.setTileType(x, y, tile);
			} else if (buildDoors && canPlaceDoor(x, y)) {
				safePlaceDoor(x, y);
			}
		}
	}
	
	public boolean canPlaceDoor(int x, int y) {
		if (level.getTileType(x, y).isWallTile()) {
			TileType[] adjacentTiles = level.getAdjacentTileTypes(x, y);
			
			for (TileType tile : adjacentTiles) {
				if (tile == TileType.TILE_ROOM_DOOR_CLOSED) {
					return false;
				}
			}
			
			return getWallOrientation(adjacentTiles) != Orientation.CORNER;
		}
		
		return false;
	}
	
	protected void safePlaceDoor(int x, int y) {
		level.setTileType(x, y, DOOR_TYPES.next());
		
		for (int[] direction : Utils.DIRECTIONS) {
			int nx = x + direction[0];
			int ny = y + direction[1];
			
			TileType t = level.getTileType(nx, ny);
			
			if (t == TileType.TILE_GROUND) {
				level.setTileType(nx, ny, TileType.TILE_CORRIDOR);
			}
		}
	}
	
	protected Orientation getWallOrientation(TileType[] adjacentTiles) {
		boolean h = adjacentTiles[0].isWallTile() || adjacentTiles[1].isWallTile();
		boolean v = adjacentTiles[2].isWallTile() || adjacentTiles[3].isWallTile();
		
		if (h && !v) {
			return Orientation.HORIZONTAL;
		} else if (!h && v) {
			return Orientation.VERTICAL;
		} else {
			return Orientation.CORNER;
		}
	}
	
	protected Orientation getWallOrientation(int x, int y) {
		return getWallOrientation(level.getAdjacentTileTypes(x, y));
	}
	
	protected ConnectionPoint getConnectionPoint(Room a, Room b) {
		int dx = Math.abs(b.getCenterX() - a.getCenterX());
		int dy = Math.abs(b.getCenterY() - a.getCenterY());
		
		if (dx > dy) {
			if (dx <= 5 || b.getCenterX() < a.getCenterX() || a.getRoomX() + a.getRoomWidth() >= b.getRoomX() || b
				.getRoomX() + b.getRoomWidth() <= a.getRoomX()) {
				if (b.getRoomX() + b.getRoomWidth() > a.getRoomX() + a.getRoomWidth()) {
					return new ConnectionPoint(
						a.getRoomX() + a.getRoomWidth() - 1, a.getCenterY(),
						b.getCenterX(), b.getRoomY() + b.getRoomHeight() - 1,
						Orientation.HORIZONTAL
					);
				} else {
					return new ConnectionPoint(
						b.getRoomX() + b.getRoomWidth() - 1, b.getCenterY(),
						a.getCenterX(), a.getRoomY() + a.getRoomHeight() - 1,
						Orientation.HORIZONTAL
					);
				}
			} else {
				if (b.getRoomX() > a.getRoomX() || b.getRoomX() + b.getRoomWidth() > a.getRoomX() + a.getRoomWidth()) {
					return new ConnectionPoint(
						a.getRoomX() + a.getRoomWidth() - 1, a.getCenterY(),
						b.getRoomX(), b.getCenterY(),
						Orientation.HORIZONTAL
					);
				} else {
					return new ConnectionPoint(
						b.getRoomX() + b.getRoomWidth() - 1, b.getCenterY(),
						a.getRoomX(), a.getCenterY(),
						Orientation.HORIZONTAL
					);
				}
			}
		} else {
			if (dy <= 5 || (b.getCenterX() - a.getCenterX() < 0) || a.getRoomY() + a.getRoomHeight() == b
				.getRoomY() || b.getRoomY() + b.getRoomHeight() == a.getRoomY()) {
				if (b.getRoomY() + b.getRoomHeight() > a.getRoomY() + a.getRoomHeight()) {
					return new ConnectionPoint(
						a.getCenterX(), a.getRoomY() + a.getRoomHeight() - 1,
						b.getRoomX() + b.getRoomWidth() - 1, b.getCenterY(),
						Orientation.VERTICAL
					);
				} else {
					return new ConnectionPoint(
						b.getCenterX(), b.getRoomY() + b.getRoomHeight() - 1,
						a.getRoomX() + a.getRoomWidth() - 1, a.getCenterY(),
						Orientation.VERTICAL
					);
				}
			} else {
				if (b.getRoomY() + b.getRoomHeight() > a.getRoomY() + a.getRoomHeight()) {
					return new ConnectionPoint(
						a.getCenterX(), a.getRoomY() + a.getRoomHeight() - 1,
						b.getCenterX(), b.getRoomY(),
						Orientation.VERTICAL
					);
				} else {
					return new ConnectionPoint(
						b.getCenterX(), b.getRoomY() + b.getRoomHeight() - 1,
						a.getCenterX(), a.getRoomY(),
						Orientation.VERTICAL
					);
				}
			}
		}
	}
	
	public enum Orientation {
		HORIZONTAL,
		VERTICAL,
		CORNER
	}
	
	public class ConnectionPoint {
		private int ax, ay;
		private int bx, by;
		
		private Orientation intendedOrientation;
		private Orientation orientationA;
		private Orientation orientationB;
		
		public ConnectionPoint(int ax, int ay, int bx, int by, Orientation intendedOrientation) {
			this.ax = ax;
			this.ay = ay;
			this.bx = bx;
			this.by = by;
			
			this.intendedOrientation = intendedOrientation;
			this.orientationA = getWallOrientation(ax, ay);
			this.orientationB = getWallOrientation(bx, by);
		}
		
		public int getAX() {
			return ax;
		}
		
		public int getAY() {
			return ay;
		}
		
		public int getBX() {
			return bx;
		}
		
		public int getBY() {
			return by;
		}
		
		public Orientation getIntendedOrientation() {
			return intendedOrientation;
		}
		
		public Orientation getOrientationA() {
			return orientationA;
		}
		
		public Orientation getOrientationB() {
			return orientationB;
		}
	}
}
