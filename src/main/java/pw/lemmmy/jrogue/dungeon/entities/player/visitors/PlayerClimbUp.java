package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public class PlayerClimbUp implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		Tile tile = player.getLevel().getTile(player.getX(), player.getY());
		
		if (tile.getType() != TileType.TILE_ROOM_STAIRS_UP && tile.getType() != TileType.TILE_ROOM_LADDER_UP) {
			player.getDungeon().log("[YELLOW]There is nothing to climb up here.[]");
			return;
		}
		
		player.climb(tile, true);
	}
}
