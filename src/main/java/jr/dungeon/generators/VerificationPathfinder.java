package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.entities.monsters.ai.AStarPathfinder;
import jr.dungeon.tiles.TileType;

import java.util.List;

public class VerificationPathfinder extends AStarPathfinder {
	@Override
	public boolean isValidLocation(Level level, int x, int y, List<TileType> avoidTiles) {
		return !(x < 0 || x >= level.getWidth() ||
			y < 0 || y >= level.getHeight()) &&
			level.getTile(x, y) != null &&
			(
				level.getTileType(x, y).getSolidity() != TileType.Solidity.SOLID ||
				level.getTileType(x, y).isDoorShut()
			) &&
			!avoidTiles.contains(level.getTileType(x, y));
	}
}
