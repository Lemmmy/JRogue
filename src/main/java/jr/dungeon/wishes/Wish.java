package jr.dungeon.wishes;

import jr.dungeon.Dungeon;
import jr.dungeon.entities.player.Player;

@FunctionalInterface
public interface Wish {
	void grant(Dungeon dungeon, Player player, String... args);
}
