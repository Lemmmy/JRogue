package jr.dungeon;

import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;
import jr.utils.Serialisable;
import jr.utils.SerialisationUtils;
import lombok.Getter;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Base64;

public class VisibilityStore implements Serialisable {
	@Getter private Boolean[] discoveredTiles;
	@Getter private Boolean[] visibleTiles;
	
	@Getter private int width;
	@Getter private int height;
	
	private Level level;
	
	public VisibilityStore(Level level) {
		this.level = level;
		
		width = level.getWidth();
		height = level.getHeight();
		
		discoveredTiles = new Boolean[width * height];
		visibleTiles = new Boolean[width * height];
		
		Arrays.fill(discoveredTiles, false);
		Arrays.fill(visibleTiles, false);
	}
	
	@Override
	public void serialise(JSONObject obj) {
		SerialisationUtils.serialiseBooleanArray(visibleTiles)
			.ifPresent(bytes -> obj.put("visibleTiles", new String(Base64.getEncoder().encode(bytes))));
		
		SerialisationUtils.serialiseBooleanArray(discoveredTiles)
			.ifPresent(bytes -> obj.put("discoveredTiles", new String(Base64.getEncoder().encode(bytes))));
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		visibleTiles = SerialisationUtils.unserialiseBooleanArray(
			Base64.getDecoder().decode(obj.getString("visibleTiles")),
			width * height
		);
		
		discoveredTiles = SerialisationUtils.unserialiseBooleanArray(
			Base64.getDecoder().decode(obj.getString("discoveredTiles")),
			width * height
		);
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
	
	public boolean isTileVisible(int x, int y) {
		return !isTileInvisible(x, y);
	}
	
	public boolean isTileVisible(Point p) {
		return isTileVisible(p.getX(), p.getY());
	}
	
	public boolean isTileInvisible(int x, int y) {
		return x < 0 || y < 0 || x >= width || y >= height || !visibleTiles[width * y + x];
	}
	
	public boolean isTileInvisible(Point p) {
		return isTileInvisible(p.getX(), p.getY());
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
				TileType type = level.getTileStore().getTileType(dx, dy);
				
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
