package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionKick;
import jr.dungeon.entities.effects.InjuredFoot;
import jr.dungeon.entities.effects.StrainedLeg;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.dungeon.io.Prompt;
import jr.utils.Directions;
import jr.utils.Point;
import jr.utils.VectorInt;

import java.util.Optional;

public class PlayerKick implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		String msg = "Kick in what direction?";
		
		player.getDungeon().prompt(new Prompt(msg, null, true, new Prompt.SimplePromptCallback(player.getDungeon()) {
			@Override
			public void onResponse(char response) {
				if (!Directions.MOVEMENT_CHARS.containsKey(response)) {
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
		
		VectorInt direction = Directions.MOVEMENT_CHARS.get(response);
		Point targetPosition = player.getPosition().add(direction);
		
		Optional<Entity> entity = player.getLevel().entityStore.getEntitiesAt(targetPosition).findFirst();
		
		if (entity.isPresent()) {
			player.setAction(new ActionKick(direction, entity.get(), new Action.NoCallback()));
		} else {
			player.setAction(new ActionKick(direction, new Action.NoCallback()));
		}
		
		player.getDungeon().turnSystem.turn();
	}
}
