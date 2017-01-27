package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;

public class PlayerClimbAny implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		Tile tile = player.getLevel().getTileStore().getTile(player.getX(), player.getY());
		
		if (tile.getType() != TileType.TILE_ROOM_STAIRS_UP && tile.getType() != TileType.TILE_ROOM_LADDER_UP &&
			tile.getType() != TileType.TILE_ROOM_STAIRS_DOWN && tile.getType() != TileType.TILE_ROOM_LADDER_DOWN) {
			player.getDungeon().log("[YELLOW]There is nothing to climb here.[]");
			return;
		}
		
		boolean up = tile.getType() == TileType.TILE_ROOM_STAIRS_UP || tile.getType() == TileType.TILE_ROOM_LADDER_UP;
		player.climb(tile, up);
	}
}
