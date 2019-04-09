package jr.dungeon.wishes;

import jr.dungeon.Dungeon;
import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.TileType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WishTile implements Wish {
	private final TileType tile;

	@Override
	public void grant(Dungeon dungeon, Player player, String... args) {
		dungeon.getLevel().tileStore.setTileType(player.getPosition(), tile);
		dungeon.turnSystem.turn();
	}
}
