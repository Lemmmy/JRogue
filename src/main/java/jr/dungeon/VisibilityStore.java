package jr.dungeon;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Solidity;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;
import jr.utils.SerialisationUtils;
import lombok.Getter;

import java.util.Arrays;
import java.util.Base64;

import static jr.utils.QuickMaths.ifloor;

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
	
	public void markTile(boolean[] arr, Point point) {
		if (!point.insideLevel(level)) return;
		arr[point.getIndex(level)] = true;
	}
	
	public boolean isTileDiscovered(Point point) {
		return point.insideLevel(level) && discoveredTiles[point.getIndex(level)];
	}

	public void discoverTile(Point point) {
		markTile(discoveredTiles, point);
	}

	public boolean isTileVisible(Point point) {
		return !isTileInvisible(point);
	}
	
	public boolean isTileInvisible(Point point) {
		return !point.insideLevel(level) || !visibleTiles[point.getIndex(level)];
	}
	
	public void seeTile(Point point) {
		markTile(visibleTiles, point);
		level.entityStore.getEntitiesAt(point).forEach(e -> e.setLastSeenPosition(point));
	}
	
	public void updateSight(Player player) {
		Arrays.fill(visibleTiles, false);
		
		for (int r = 0; r < 360; r++) {
			int corridorVisibility = 0;
			boolean breakNext = false;
			
			for (int i = 0; i < player.getVisibilityRange(); i++) {
				double a = Math.toRadians(r);
				
				Point pos = Point.get(
					ifloor(player.getPosition().x + i * Math.cos(a)),
					ifloor(player.getPosition().y + i * Math.sin(a))
				);
				
				TileType type = level.tileStore.getTileType(pos);
				
				if (type == TileType.TILE_CORRIDOR) {
					corridorVisibility += 1;
				}
				
				if (corridorVisibility >= player.getCorridorVisibilityRange()) {
					break;
				}
				
				discoverTile(pos);
				seeTile(pos);
				
				if (
					!pos.insideLevel(level) ||
					type.getSolidity() == Solidity.SOLID ||
					!pos.equals(player.getPosition()) && type.isSemiTransparent() ||
					breakNext
				) {
					break;
				}
				
				if (pos.equals(player.getPosition()) && type.isSemiTransparent()) {
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
