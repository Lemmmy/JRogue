package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.Prompt;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionKick;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.effects.InjuredFoot;
import pw.lemmmy.jrogue.dungeon.entities.effects.StrainedLeg;
import pw.lemmmy.jrogue.dungeon.entities.player.Attribute;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.utils.Utils;

public class PlayerKick implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		String msg = "Kick in what direction?";
		
		player.getDungeon().prompt(new Prompt(msg, null, true, new Prompt.SimplePromptCallback(player.getDungeon()) {
			@Override
			public void onResponse(char response) {
				if (!Utils.MOVEMENT_CHARS.containsKey(response)) {
					player.getDungeon().log(String.format("Invalid direction '[YELLOW]%s[]'.", response));
					return;
				}
				
				kick(response, player);
			}
		}));
	}
	
	private void kick(char response, Player player) {
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
		
		Integer[] d = Utils.MOVEMENT_CHARS.get(response);
		int dx = d[0];
		int dy = d[1];
		
		if (player.getLevel().getEntitiesAt(player.getX() + dx, player.getY() + dy).size() > 0) {
			player.setAction(new ActionKick(
				d,
				player.getLevel().getEntitiesAt(player.getX() + dx, player.getY() + dy).get(0),
				new EntityAction.NoCallback()
			));
		} else {
			player.setAction(new ActionKick(d, new EntityAction.NoCallback()));
		}
		
		player.getDungeon().turn();
	}
}
