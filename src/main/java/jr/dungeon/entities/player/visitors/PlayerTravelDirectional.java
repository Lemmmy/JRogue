package jr.dungeon.entities.player.visitors;

import jr.dungeon.Prompt;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMove;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.PathShowEvent;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.Path;
import jr.utils.Point;
import jr.utils.Utils;
import jr.utils.VectorInt;

public class PlayerTravelDirectional implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		String msg = "Travel in what direction?";
		
		player.getDungeon().prompt(new Prompt(msg, null, true, new Prompt.SimplePromptCallback(player.getDungeon()) {
			@Override
			public void onResponse(char response) {
				if (!Utils.MOVEMENT_CHARS.containsKey(response)) {
					player.getDungeon().log(String.format("Invalid direction '[YELLOW]%s[]'.", response));
					return;
				}
				
				travel(response, player);
			}
		}));
	}
	
	private void travel(char response, Player player) {
		Path pathTaken = new Path();
		
		VectorInt d = Utils.MOVEMENT_CHARS.get(response);
		int dx = d.getX();
		int dy = d.getY();
		
		for (int i = 0; i < 50; i++) { // max 50 steps in one move
			Tile destTile = player.getLevel().getTileStore()
				.getTile(player.getX() + dx, player.getY() + dy);
			
			if (
				destTile == null ||
					i >= 1 && destTile.getType().getSolidity() == TileType.Solidity.WALK_THROUGH ||
					destTile.getType().getSolidity() == TileType.Solidity.SOLID
				) {
				break;
			}
			
			Point oldPos = player.getPosition();
			
			pathTaken.addStep(destTile);
			player.setAction(new ActionMove(player.getX() + dx, player.getY() + dy, new Action.NoCallback()));
			player.getDungeon().turn();
			
			if (oldPos.equals(player.getPosition())) { // we didn't go anywhere, so stop
				break;
			}
			
			if (i > 2 && player.getLevel().getEntityStore()
				.getAdjacentMonsters(player.getX(), player.getY()).size() > 0) {
				break;
			}
		}
		
		player.getDungeon().triggerEvent(new PathShowEvent(pathTaken));
	}
}
