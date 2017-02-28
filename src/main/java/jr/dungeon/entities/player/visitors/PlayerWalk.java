package jr.dungeon.entities.player.visitors;

import jr.dungeon.Prompt;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.ActionKick;
import jr.dungeon.entities.actions.ActionMove;
import jr.dungeon.entities.actions.EntityAction;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.weapons.ItemWeaponMelee;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class PlayerWalk implements PlayerVisitor {
	private int dx, dy;
	
	@Override
	public void visit(Player player) {
		dx = Math.max(-1, Math.min(1, dx));
		dy = Math.max(-1, Math.min(1, dy));
		
		int newX = player.getX() + dx;
		int newY = player.getY() + dy;
		
		Tile tile = player.getLevel().getTileStore().getTile(newX, newY);
		
		if (tile == null) {
			return;
		}
		
		List<Entity> destEntities = player.getLevel().getEntityStore().getEntitiesAt(newX, newY);
		
		if (destEntities.size() > 0) {
			// TODO: Ask the player to confirm if they want to attack something silly (e.g. their familiar or a clerk)
			
			Optional<Entity> ent = destEntities.stream()
				.filter(e -> e instanceof EntityLiving)
				.findFirst();
			
			if (ent.isPresent()) {
				if (player.getRightHand() != null && player.getRightHand().getItem() instanceof ItemWeaponMelee) {
					((ItemWeaponMelee) player.getRightHand().getItem()).hit(player, (EntityLiving) ent.get());
				} else if (player.getLeftHand() != null && player.getLeftHand().getItem() instanceof ItemWeaponMelee) {
					((ItemWeaponMelee) player.getLeftHand().getItem()).hit(player, (EntityLiving) ent.get());
				} else {
					player.getDungeon().You("have no weapon equipped!"); // TODO: Make it possible to attack bare-handed
				}
			} else {
				walkAction(player, tile, newX, newY);
			}
		} else {
			walkAction(player, tile, newX, newY);
		} // TODO: Restructure this mess
		
		player.getDungeon().turn();
	}
	
	private void walkAction(Player player, Tile tile, int x, int y) {
		if (dx != 0 && dy != 0 && tile.getType().isDoor()) {
			// prevent diagonal movement to a door - the player cannot reach the handle
			return;
		}
		
		if (tile.getType().getSolidity() != TileType.Solidity.SOLID) {
			player.setAction(new ActionMove(x, y, new EntityAction.NoCallback()));
		} else if (tile.getType() == TileType.TILE_ROOM_DOOR_LOCKED) {
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
