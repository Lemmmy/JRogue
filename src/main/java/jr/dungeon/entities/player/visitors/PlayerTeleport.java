package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionTeleport;
import jr.dungeon.entities.player.Player;
import jr.utils.Point;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerTeleport implements PlayerVisitor {
    private Point position;
    
    @Override
    public void visit(Player player) {
        player.setAction(new ActionTeleport(position, new Action.NoCallback()));
        player.getDungeon().turnSystem.turn();
    }
}
