package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.Prompt;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMove;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionTeleport;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.Path;
import pw.lemmmy.jrogue.utils.Utils;

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
		
		Integer[] d = Utils.MOVEMENT_CHARS.get(response);
		int dx = d[0];
		int dy = d[1];
		
		for (int i = 0; i < 50; i++) { // max 50 steps in one move
			Tile destTile = player.getLevel().getTile(player.getX() + dx, player.getY() + dy);
			
			if (
				destTile == null ||
					i >= 1 && destTile.getType().getSolidity() == TileType.Solidity.WALK_THROUGH ||
					destTile.getType().getSolidity() == TileType.Solidity.SOLID
				) {
				break;
			}
			
			int oldX = player.getX();
			int oldY = player.getY();
			
			pathTaken.addStep(destTile);
			player.setAction(new ActionMove(player.getX() + dx, player.getY() + dy, new EntityAction.NoCallback()));
			player.getDungeon().turn();
			
			if (oldX == player.getX() && oldY == player.getY()) { // we didn't go anywhere, so stop
				break;
			}
			
			if (i > 2 && player.getLevel().getAdjacentMonsters(player.getX(), player.getY()).size() > 0) {
				break;
			}
		}
		
		player.getDungeon().showPath(pathTaken);
	}
}
