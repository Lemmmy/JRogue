package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionTeleport;
import jr.dungeon.entities.player.Player;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerTeleport implements PlayerVisitor {
	private int x, y;
	
	@Override
	public void visit(Player player) {
		player.setAction(new ActionTeleport(x, y, new Action.NoCallback()));
		player.getDungeon().turn();
	}
}
