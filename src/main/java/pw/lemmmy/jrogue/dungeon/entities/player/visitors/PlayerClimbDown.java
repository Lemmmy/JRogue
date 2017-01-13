package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public class PlayerClimbDown implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		Tile tile = player.getLevel().getTile(player.getX(), player.getY());
		
		if (tile.getType() != TileType.TILE_ROOM_STAIRS_DOWN && tile.getType() != TileType.TILE_ROOM_LADDER_DOWN) {
			player.getDungeon().log("[YELLOW]There is nothing to climb down here.[]");
			return;
		}
		
		player.climb(tile, false);
	}
}
