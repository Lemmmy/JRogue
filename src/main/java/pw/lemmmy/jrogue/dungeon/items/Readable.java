package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.entities.player.Player;

public interface Readable {
	void onRead(Player reader);
}
