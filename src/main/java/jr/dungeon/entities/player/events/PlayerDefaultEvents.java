package jr.dungeon.entities.player.events;

import jr.dungeon.Prompt;
import jr.dungeon.entities.actions.ActionKick;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.events.DungeonEventListener;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;

public class PlayerDefaultEvents implements DungeonEventListener {
	@DungeonEventHandler
	public void onPlayerWalkedIntoSolidEvent(PlayerWalkedIntoSolidEvent e) {
		Tile tile = e.getTile();
		Player player = e.getPlayer();
		int x = e.getX();
		int y = e.getY();
		int dx = e.getDirectionX();
		int dy = e.getDirectionY();
		
		if (tile.getType() == TileType.TILE_ROOM_DOOR_LOCKED) {
			player.getDungeon().prompt(new Prompt(
				"The door is locked. Kick it down?",
				new char[]{'y', 'n'},
				true,
				new Prompt.SimplePromptCallback(player.getDungeon()) {
					@Override
					public void onResponse(char response) {
						if (response == 'n') {
							player.getDungeon().log("Nevermind.");
							return;
						}
						
						for (int i = 0; i < 15; i++) {
							player.setAction(new ActionKick(new Integer[]{dx, dy}, null));
							player.getDungeon().turn();
							
							if (tile.getType() != TileType.TILE_ROOM_DOOR_LOCKED) {
								return;
							}
						}
						
						player.getDungeon().log("Unable to kick the door down after 15 turns.");
					}
				}
			));
		} else if (tile.getType() == TileType.TILE_ROOM_DOOR_CLOSED) {
			tile.setType(TileType.TILE_ROOM_DOOR_OPEN);
			player.getDungeon().You("open the door.");
		}
	}
}
