package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMove;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.PathShowEvent;
import jr.dungeon.io.Prompt;
import jr.dungeon.tiles.Solidity;
import jr.dungeon.tiles.Tile;
import jr.utils.Directions;
import jr.utils.Path;
import jr.utils.Point;
import jr.utils.VectorInt;

public class PlayerTravelDirectional implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		String msg = "Travel in what direction?";
		
		player.getDungeon().prompt(new Prompt(msg, null, true, new Prompt.SimplePromptCallback(player.getDungeon()) {
			@Override
			public void onResponse(char response) {
				if (!Directions.MOVEMENT_CHARS.containsKey(response)) {
					player.getDungeon().log(String.format("Invalid direction '[YELLOW]%s[]'.", response));
					return;
				}
				
				travel(response, player);
			}
		}));
	}
	
	private void travel(char response, Player player) {
		Path pathTaken = new Path();
		
		VectorInt direction = Directions.MOVEMENT_CHARS.get(response);
		Point newPosition = player.getPosition().add(direction);
		
		for (int i = 0; i < 50; i++) { // max 50 steps in one move
			Tile destTile = player.getLevel().tileStore.getTile(newPosition);
			
			if (
				destTile == null ||
				i >= 1 && destTile.getType().getSolidity() == Solidity.WALK_THROUGH ||
				destTile.getType().getSolidity() == Solidity.SOLID
			) {
				break;
			}
			
			Point oldPos = player.getPosition();
			
			pathTaken.addStep(destTile);
			player.setAction(new ActionMove(newPosition, new Action.NoCallback()));
			player.getDungeon().turnSystem.turn();
			
			if (oldPos.equals(player.getPosition())) { // we didn't go anywhere, so stop
				break;
			}
			
			if (i > 2 && player.getLevel().entityStore.getAdjacentMonsters(newPosition).findAny().isPresent()) {
				break;
			}
		}
		
		player.getDungeon().eventSystem.triggerEvent(new PathShowEvent(pathTaken));
	}
}
