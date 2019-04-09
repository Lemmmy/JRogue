package jr.dungeon.generators;

import jr.dungeon.Level;
import jr.dungeon.entities.monsters.ai.AStarPathfinder;
import jr.dungeon.tiles.Solidity;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;

import java.util.List;

public class VerificationPathfinder extends AStarPathfinder {
    @Override
    public boolean isValidLocation(Level level, Point point, List<TileType> avoidTiles) {
        return point.insideLevel(level) &&
            level.tileStore.getTile(point) != null &&
            (
                level.tileStore.getTileType(point).getSolidity() != Solidity.SOLID ||
                level.tileStore.getTileType(point).isDoorShut()
            ) &&
            !avoidTiles.contains(level.tileStore.getTileType(point));
    }
}
