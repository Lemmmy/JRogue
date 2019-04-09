package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.player.Player;

public interface PlayerVisitor {
    void visit(Player player);
}
