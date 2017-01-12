package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.entities.actions.ActionTeleport;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;

public class PlayerTeleport implements PlayerVisitor {
	private int x, y;
	
	public PlayerTeleport(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void visit(Player player) {
		player.setAction(new ActionTeleport(x, y, new EntityAction.NoCallback()));
		player.getDungeon().turn();
	}
}
