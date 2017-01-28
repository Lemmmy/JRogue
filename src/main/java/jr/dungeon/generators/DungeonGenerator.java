package jr.dungeon.generators;

import com.github.alexeyr.pcg.Pcg32;
import jr.dungeon.Level;
import jr.dungeon.generators.rooms.Room;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.Utils;
import jr.utils.WeightedCollection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Random;

public abstract class DungeonGenerator {
	private static final WeightedCollection<TileType> DOOR_TYPES = new WeightedCollection<>();
	
	static {
		DOOR_TYPES.add(3, TileType.TILE_ROOM_DOOR_LOCKED);
		DOOR_TYPES.add(4, TileType.TILE_ROOM_DOOR_CLOSED);
		DOOR_TYPES.add(6, TileType.TILE_ROOM_DOOR_OPEN);
	}
	
	@Getter protected Level level;
	@Getter protected Tile sourceTile;
	
	protected Pcg32 rand = new Pcg32();
	protected Random jrand = new Random();
	
	public DungeonGenerator(Level level, Tile sourceTile) {
		this.level = level;
		this.sourceTile = sourceTile;
	}
	
	public abstract Climate getClimate();
	
	public abstract MonsterSpawningStrategy getMonsterSpawningStrategy();
	
	public abstract boolean generate();
	
	protected int nextInt(int min, int max) {
		return rand.nextInt(max - min) + min;
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
			
			if (level.getTileStore().getTileType(x, y).isBuildable()) {
				level.getTileStore().setTileType(x, y, tile);
			} else if (buildDoors && canPlaceDoor(x, y)) {
				safePlaceDoor(x, y);
			}
		}
	}
	
	public boolean canPlaceDoor(int x, int y) {
		if (level.getTileStore().getTileType(x, y).isWallTile()) {
			TileType[] adjacentTiles = level.getTileStore().getAdjacentTileTypes(x, y);
			
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
		level.getTileStore().setTileType(x, y, DOOR_TYPES.next());
		
		for (int[] direction : Utils.DIRECTIONS) {
			int nx = x + direction[0];
			int ny = y + direction[1];
			
			TileType t = level.getTileStore().getTileType(nx, ny);
			
			if (t == TileType.TILE_GROUND) {
				level.getTileStore().setTileType(nx, ny, TileType.TILE_CORRIDOR);
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
		return getWallOrientation(level.getTileStore().getAdjacentTileTypes(x, y));
	}
	
	protected ConnectionPoint getConnectionPoint(Room a, Room b) {
		int dx = Math.abs(b.getCenterX() - a.getCenterX());
		int dy = Math.abs(b.getCenterY() - a.getCenterY());
		
		if (dx > dy) {
			if (dx <= 5 || b.getCenterX() < a.getCenterX() || a.getX() + a.getWidth() >= b.getX() || b
				.getX() + b.getWidth() <= a.getX()) {
				if (b.getX() + b.getWidth() > a.getX() + a.getWidth()) {
					return new ConnectionPoint(
						a.getX() + a.getWidth() - 1, a.getCenterY(),
						b.getCenterX(), b.getY() + b.getHeight() - 1,
						Orientation.HORIZONTAL
					);
				} else {
					return new ConnectionPoint(
						b.getX() + b.getWidth() - 1, b.getCenterY(),
						a.getCenterX(), a.getY() + a.getHeight() - 1,
						Orientation.HORIZONTAL
					);
				}
			} else {
				if (b.getX() > a.getX() || b.getX() + b.getWidth() > a.getX() + a.getWidth()) {
					return new ConnectionPoint(
						a.getX() + a.getWidth() - 1, a.getCenterY(),
						b.getX(), b.getCenterY(),
						Orientation.HORIZONTAL
					);
				} else {
					return new ConnectionPoint(
						b.getX() + b.getWidth() - 1, b.getCenterY(),
						a.getX(), a.getCenterY(),
						Orientation.HORIZONTAL
					);
				}
			}
		} else {
			if (dy <= 5 || b.getCenterX() - a.getCenterX() < 0 || a.getY() + a.getHeight() == b
				.getY() || b.getY() + b.getHeight() == a.getY()) {
				if (b.getY() + b.getHeight() > a.getY() + a.getHeight()) {
					return new ConnectionPoint(
						a.getCenterX(), a.getY() + a.getHeight() - 1,
						b.getX() + b.getWidth() - 1, b.getCenterY(),
						Orientation.VERTICAL
					);
				} else {
					return new ConnectionPoint(
						b.getCenterX(), b.getY() + b.getHeight() - 1,
						a.getX() + a.getWidth() - 1, a.getCenterY(),
						Orientation.VERTICAL
					);
				}
			} else {
				if (b.getY() + b.getHeight() > a.getY() + a.getHeight()) {
					return new ConnectionPoint(
						a.getCenterX(), a.getY() + a.getHeight() - 1,
						b.getCenterX(), b.getY(),
						Orientation.VERTICAL
					);
				} else {
					return new ConnectionPoint(
						b.getCenterX(), b.getY() + b.getHeight() - 1,
						a.getCenterX(), a.getY(),
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
	
	@Getter
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
	}
}
