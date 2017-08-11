package jr.dungeon.entities.player.visitors;

import jr.dungeon.io.DirectionPromptCallback;
import jr.dungeon.io.Prompt;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionKick;
import jr.dungeon.entities.effects.InjuredFoot;
import jr.dungeon.entities.effects.StrainedLeg;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.utils.Utils;
import jr.utils.VectorInt;

public class PlayerKick implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		String msg = "Kick in what direction?";
		
		player.getDungeon().prompt(new Prompt(msg, null, true, new DirectionPromptCallback(player.getDungeon()) {
			@Override
			public void onDirectionResponse(VectorInt dir) {
				kick(dir, player);
			}
		}));
	}
	
	private void kick(VectorInt d, Player player) {
		int wisdom = player.getAttributes().getAttribute(Attribute.WISDOM);
		
		if (wisdom > 5) {
			if (player.hasStatusEffect(InjuredFoot.class)) {
				player.getDungeon().Your("foot is in no shape for kicking.");
				return;
			}
			
			if (player.hasStatusEffect(StrainedLeg.class)) {
				player.getDungeon().Your("leg is in no shape for kicking.");
				return;
			}
		}
		
		int dx = d.getX();
		int dy = d.getY();
		
		if (player.getLevel().entityStore.getEntitiesAt(player.getX() + dx, player.getY() + dy).size() > 0) {
			player.setAction(new ActionKick(
				d,
				player.getLevel().entityStore.getEntitiesAt(player.getX() + dx, player.getY() + dy).get(0),
				new Action.NoCallback()
			));
		} else {
			player.setAction(new ActionKick(d, new Action.NoCallback()));
		}
		
		player.getDungeon().turnSystem.turn(player.getDungeon());
	}
}
