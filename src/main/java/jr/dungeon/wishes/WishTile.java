package jr.dungeon.wishes;

import jr.dungeon.Dungeon;
import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.TileType;

public class WishTile implements Wish {
	private final TileType tile;

	public WishTile(TileType tile) {
		this.tile = tile;
	}

	@Override
	public void grant(Dungeon dungeon, Player player, String... args) {
		dungeon.getLevel().getTileStore().setTileType(player.getX(), player.getY(), tile);
		dungeon.turn();
	}
}
