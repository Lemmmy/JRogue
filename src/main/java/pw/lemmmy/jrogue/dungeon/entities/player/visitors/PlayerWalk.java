package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMove;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.weapons.ItemWeaponMelee;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

import java.util.List;
import java.util.Optional;

public class PlayerWalk implements PlayerVisitor {
	private int dx, dy;
	
	public PlayerWalk(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	@Override
	public void visit(Player player) {
		dx = Math.max(-1, Math.min(1, dx));
		dy = Math.max(-1, Math.min(1, dy));
		
		int newX = player.getX() + dx;
		int newY = player.getY() + dy;
		
		Tile tile = player.getLevel().getTile(newX, newY);
		
		if (tile == null) {
			return;
		}
		
		if (dx != 0 && dy != 0 && tile.getType().isDoor()) {
			// prevent diagonal movement to a door - the player cannot reach the handle
			return;
		}
		
		List<Entity> destEntities = player.getLevel().getEntitiesAt(newX, newY);
		
		if (destEntities.size() > 0) {
			// TODO: Ask the player to confirm if they want to attack something silly (e.g. their familiar or a clerk)
			
			Optional<Entity> ent = destEntities.stream()
				.filter(e -> e instanceof LivingEntity)
				.findFirst();
			
			if (ent.isPresent()) {
				if (player.getRightHand() != null && player.getRightHand().getItem() instanceof ItemWeaponMelee) {
					((ItemWeaponMelee) player.getRightHand().getItem()).hit(player, (LivingEntity) ent.get());
				} else if (player.getLeftHand() != null && player.getLeftHand().getItem() instanceof ItemWeaponMelee) {
					((ItemWeaponMelee) player.getLeftHand().getItem()).hit(player, (LivingEntity) ent.get());
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
		if (tile.getType().getSolidity() != TileType.Solidity.SOLID) {
			player.setAction(new ActionMove(x, y, new EntityAction.NoCallback()));
		} else if (tile.getType() == TileType.TILE_ROOM_DOOR_LOCKED) {
			player.getDungeon().The("door is locked.");
		} else if (tile.getType() == TileType.TILE_ROOM_DOOR_CLOSED) {
			tile.setType(TileType.TILE_ROOM_DOOR_OPEN);
			player.getDungeon().You("open the door.");
		}
	}
}
