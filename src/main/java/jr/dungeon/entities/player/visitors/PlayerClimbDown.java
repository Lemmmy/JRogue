package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileFlag;
import jr.dungeon.tiles.TileType;

public class PlayerClimbDown implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		Tile tile = player.getLevel().getTileStore().getTile(player.getX(), player.getY());
		
		if ((tile.getType().getFlags() & TileFlag.DOWN) == TileFlag.DOWN) {
			player.getDungeon().log("[YELLOW]There is nothing to climb down here.[]");
			return;
		}
		
		player.defaultVisitors.climb(tile, false);
	}
}
