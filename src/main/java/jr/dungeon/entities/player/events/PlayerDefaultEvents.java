package jr.dungeon.entities.player.events;

import jr.dungeon.io.Prompt;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.actions.ActionKick;
import jr.dungeon.entities.effects.InjuredFoot;
import jr.dungeon.entities.events.EntityKickedTileEvent;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventListener;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.states.TileStateDoor;
import jr.utils.RandomUtils;
import jr.utils.VectorInt;

public class PlayerDefaultEvents implements EventListener {
	@EventHandler
	private void onPlayerWalkedIntoSolidEvent(PlayerWalkedIntoSolidEvent e) {
		Player player = e.getPlayer();
		Tile tile = e.getTile();
		int x = e.getX();
		int y = e.getY();
		int dx = e.getDirectionX();
		int dy = e.getDirectionY();
		
		if (tile.getType() == TileType.TILE_ROOM_DOOR_LOCKED) {
			onPlayerWalkedLockedDoor(player, tile, dx, dy);
		} else if (tile.getType() == TileType.TILE_ROOM_DOOR_CLOSED) {
			onPlayerWalkedClosedDoor(player, tile);
		}
	}
	
	private void onPlayerWalkedLockedDoor(Player player, Tile tile, int dx, int dy) {
		String msg = "The door is locked. Kick it down?";
		
		player.getDungeon().prompt(new Prompt(msg, new char[]{'y', 'n'}, true, new Prompt.SimplePromptCallback(player.getDungeon()) {
			@Override
			public void onResponse(char response) {
				if (response == 'n') {
					player.getDungeon().log("Nevermind.");
					return;
				}
				
				for (int i = 0; i < 15; i++) {
					if (i != 0) {
						player.getDungeon().turnSystem.setDoingBulkAction(true);
					}
					
					player.setAction(new ActionKick(new VectorInt(dx, dy), null));
					player.getDungeon().turnSystem.turn(player.getDungeon());
					
					if (tile.getType() != TileType.TILE_ROOM_DOOR_LOCKED) {
						player.getDungeon().turnSystem.setDoingBulkAction(false);
						return;
					}
					
					if (player.getDungeon().turnSystem.isSomethingHappened()) {
						player.getDungeon().turnSystem.setDoingBulkAction(false);
						player.getDungeon().log("You stop kicking the door.");
						return;
					}
				}
				
				player.getDungeon().turnSystem.setDoingBulkAction(false);
				
				player.getDungeon().log("Unable to kick the door down after 15 turns.");
			}
		}));
	}
	
	private void onPlayerWalkedClosedDoor(Player player, Tile tile) {
		tile.setType(TileType.TILE_ROOM_DOOR_OPEN);
		player.getDungeon().You("open the door.");
	}
	
	@EventHandler
	public void onPlayerKickedTileEvent(EntityKickedTileEvent e) {
		if (!e.isKickerPlayer()) {
			return;
		}
		
		Tile tile = e.getTile();
		TileType tileType = tile.getType();
		Player player = (Player) e.getKicker();
		
		if (tileType.isDoorShut() && tile.hasState() && tile.getState() instanceof TileStateDoor) {
			onPlayerKickedDoor(tile, tileType, player);
		} else if (tileType.isWallTile()) {
			onPlayerKickedWall(player);
		} else {
			player.getDungeon().You("kick it!");
		}
	}
	
	private void onPlayerKickedDoor(Tile tile, TileType tileType, Player player) {
		int strength = player.getAttributes().getAttribute(Attribute.STRENGTH);
		int damage = RandomUtils.roll((int) Math.ceil(strength / 8) + 1);
		
		TileStateDoor doorState = (TileStateDoor) tile.getState();
		
		if (doorState.damage(damage) > 0) {
			player.getDungeon().logRandom(
				"WHAMM!!",
				"CRASH!!"
			);
		} else {
			player.getDungeon().logRandom(
				"The door crashes open!",
				"The door falls off its hinges!",
				"You kick the door off its hinges!",
				"You kick the door down!"
			);
		}
	}
	
	private void onPlayerKickedWall(Player player) {
		player.getDungeon().You("kick the wall!");
		
		if (RandomUtils.roll(5) == 1) {
			if (player.getAttributes().getAttribute(Attribute.STRENGTH) >= 12) {
				return;
			}
			
			player.getDungeon().logRandom(
				"[RED]Ouch! That hurt a lot!",
				"[RED]Ouch! That caused some bad damage to your foot!"
			);
			
			player.damage(new DamageSource(player, null, DamageType.KICKING_A_WALL), 1);
			player.addStatusEffect(new InjuredFoot(player.getDungeon(), player, RandomUtils.roll(3, 6)));
		} else {
			player.getDungeon().log("[ORANGE]Ouch! That hurt!");
		}
	}
}
