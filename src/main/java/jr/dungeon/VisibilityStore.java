package jr.dungeon;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;
import jr.utils.SerialisationUtils;
import lombok.Getter;

import java.util.Arrays;
import java.util.Base64;

public class VisibilityStore implements LevelStore {
	@Getter private boolean[] discoveredTiles;
	@Getter private boolean[] visibleTiles;
	
	@Getter private int width;
	@Getter private int height;
	
	private Level level;
	
	public VisibilityStore(Level level) {
		this.level = level;
	}
	
	public void initialise() {
		width = level.getWidth();
		height = level.getHeight();
		
		discoveredTiles = new boolean[width * height];
		visibleTiles = new boolean[width * height];
		
		Arrays.fill(discoveredTiles, false);
		Arrays.fill(visibleTiles, false);
	}
	
	@Override
	public void serialise(Gson gson, JsonObject out) {
		out.addProperty("visibleTiles", new String(Base64.getEncoder().encode(
			SerialisationUtils.serialiseBooleanArray(visibleTiles))));
		
		out.addProperty("discoveredTiles", new String(Base64.getEncoder().encode(
			SerialisationUtils.serialiseBooleanArray(discoveredTiles))));
	}
	
	@Override
	public void deserialise(Gson gson, JsonObject in) {
		visibleTiles = SerialisationUtils.deserialiseBooleanArray(
			Base64.getDecoder().decode(in.get("visibleTiles").getAsString()),
			width * height
		);
		
		discoveredTiles = SerialisationUtils.deserialiseBooleanArray(
			Base64.getDecoder().decode(in.get("discoveredTiles").getAsString()),
			width * height
		);
	}
	
	public void markTile(boolean[] arr, int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) return;
		arr[y * width + x] = true;
	}
	
	public boolean isTileDiscovered(int x, int y) {
		return x >= 0 && y >= 0 && x < width && y < height && discoveredTiles[y * width + x];
	}

	public boolean isTileDiscovered(Point p) {
		return isTileDiscovered(p.getX(), p.getY());
	}

	public void discoverTile(int x, int y) {
		markTile(discoveredTiles, x, y);
	}

	public void discoverTile(Point p) {
		discoverTile(p.getX(), p.getY());
	}

	public boolean isTileVisible(int x, int y) {
		return !isTileInvisible(x, y);
	}
	
	public boolean isTileVisible(Point p) {
		return isTileVisible(p.getX(), p.getY());
	}
	
	public boolean isTileInvisible(int x, int y) {
		return x < 0 || y < 0 || x >= width || y >= height || !visibleTiles[y * width + x];
	}
	
	public boolean isTileInvisible(Point p) {
		return isTileInvisible(p.getX(), p.getY());
	}
	
	public void seeTile(int x, int y) {
		markTile(visibleTiles, x, y);
		
		level.entityStore.getEntities().stream()
			.filter(e -> e.getX() == x && e.getY() == y)
			.forEach(e -> {
				e.setLastSeenX(x);
				e.setLastSeenY(y);
			});
	}

	public void seeTile(Point p) {
		seeTile(p.getX(), p.getY());
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
				TileType type = level.tileStore.getTileType(dx, dy);
				
				if (type == TileType.TILE_CORRIDOR) {
					corridorVisibility += 1;
				}
				
				if (corridorVisibility >= player.getCorridorVisibilityRange()) {
					break;
				}
				
				discoverTile(dx, dy);
				seeTile(dx, dy);
				
				if (
					dx < 0 || dy < 0 || dx >= width || dy >= height ||
					type.getSolidity() == TileType.Solidity.SOLID ||
					!(dx == player.getX() && dy == player.getY()) && type.isSemiTransparent() ||
					breakNext
				) {
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
}
