package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileFlag;

public class PlayerClimbAny implements PlayerVisitor {
    @Override
    public void visit(Player player) {
        Tile tile = player.getLevel().tileStore.getTile(player.getPosition());
        
        if ((tile.getType().getFlags() & TileFlag.CLIMBABLE) == TileFlag.CLIMBABLE) {
            player.getDungeon().log("[YELLOW]There is nothing to climb here.[]");
            return;
        }
        
        boolean up = (tile.getType().getFlags() & TileFlag.UP) == TileFlag.UP;
        player.defaultVisitors.climb(tile, up);
    }
}
