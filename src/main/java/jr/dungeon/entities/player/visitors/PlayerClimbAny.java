package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileFlag;
import jr.dungeon.tiles.TileType;

public class PlayerClimbAny implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		Tile tile = player.getLevel().getTileStore().getTile(player.getX(), player.getY());
		
		if ((tile.getType().getFlags() & TileFlag.CLIMBABLE) == TileFlag.CLIMBABLE) {
			player.getDungeon().log("[YELLOW]There is nothing to climb here.[]");
			return;
		}
		
		boolean up = (tile.getType().getFlags() & TileFlag.UP) == TileFlag.UP;
		player.defaultVisitors.climb(tile, up);
	}
}
