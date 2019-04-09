package jr.dungeon.items;

import jr.dungeon.entities.player.Player;

public interface ReadableItem {
	void onRead(Player reader);
}
