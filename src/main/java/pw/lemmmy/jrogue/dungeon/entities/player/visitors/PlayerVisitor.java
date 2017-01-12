package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.entities.player.Player;

public interface PlayerVisitor {
	void visit(Player player);
}
