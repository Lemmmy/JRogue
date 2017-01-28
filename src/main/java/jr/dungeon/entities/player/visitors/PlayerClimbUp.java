package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;

public class PlayerClimbUp implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		Tile tile = player.getLevel().getTileStore().getTile(player.getX(), player.getY());
		
		if (tile.getType() != TileType.TILE_ROOM_STAIRS_UP && tile.getType() != TileType.TILE_ROOM_LADDER_UP) {
			player.getDungeon().log("[YELLOW]There is nothing to climb up here.[]");
			return;
		}
		
		player.acceptVisitor(new PlayerClimb(tile, true));
	}
}
