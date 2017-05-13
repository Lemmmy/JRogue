package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMove;
import jr.dungeon.entities.interfaces.Friendly;
import jr.dungeon.entities.monsters.familiars.Familiar;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.player.events.PlayerWalkedIntoSolidEvent;
import jr.dungeon.io.Prompt;
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
		
		Tile tile = player.getLevel().tileStore.getTile(newX, newY);
		
		if (tile == null) {
			return;
		}
		
		List<Entity> destEntities = player.getLevel().entityStore.getEntitiesAt(newX, newY);
		
		boolean acted = true;
		
		if (destEntities.size() > 0) {
			Optional<Entity> optionalEnt = destEntities.stream()
				.filter(e -> e instanceof EntityLiving)
				.findFirst();
			
			// TODO: if multiple entities occupy this tile, popup a dialog asking which one to attack
			
			if (optionalEnt.isPresent()) {
				Entity ent = optionalEnt.get();
				
				if (ent instanceof Friendly && ((Friendly) ent).isFriendly()) {
					friendlyQuery(player, ent);
					acted = false;
				} else {
					meleeAction(player, ent);
				}
			} else {
				walkAction(player, tile, newX, newY);
			}
		} else {
			walkAction(player, tile, newX, newY);
		}
		
		if (acted) {
			player.getDungeon().turnSystem.turn(player.getDungeon());
		}
	}
	
	private void friendlyQuery(Player player, Entity ent) {
		String msg = "Are you sure you want to attack [WHITE]" + ent.getName(player, true) + "[]?";
		
		player.getDungeon().prompt(new Prompt(
			msg, new char[]{'y', 'n'}, true,
			new Prompt.SimplePromptCallback(player.getDungeon()) {
				@Override
				public void onResponse(char response) {
					if (response == 'y') {
						meleeAction(player, ent);
						player.getDungeon().turnSystem.turn(player.getDungeon());
					}
				}
			}
		));
	}
	
	private void meleeAction(Player player, Entity ent) {
		if (player.getRightHand() != null && player.getRightHand().getItem() instanceof ItemWeaponMelee) {
			((ItemWeaponMelee) player.getRightHand().getItem()).hit(player, (EntityLiving) ent);
		} else if (player.getLeftHand() != null && player.getLeftHand().getItem() instanceof ItemWeaponMelee) {
			((ItemWeaponMelee) player.getLeftHand().getItem()).hit(player, (EntityLiving) ent);
		} else {
			player.getDungeon().You("have no weapon equipped!"); // TODO: Make it possible to attack bare-handed
		}
	}
	
	private void walkAction(Player player, Tile tile, int x, int y) {
		if (tile.getType().getSolidity() != TileType.Solidity.SOLID) {
			player.setAction(new ActionMove(x, y, new Action.NoCallback()));
		} else {
			player.getDungeon().eventSystem
				.triggerEvent(new PlayerWalkedIntoSolidEvent(player, tile, x, y, dx, dy));
		}
	}
}
