package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.InjuredFoot;
import pw.lemmmy.jrogue.dungeon.entities.effects.StrainedLeg;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileStateDoor;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.Utils;

public class ActionKick extends EntityAction {
	private Integer[] direction;
	private Entity kickedEntity;
	
	public ActionKick(Dungeon dungeon, Entity entity, Integer[] direction) {
		this(dungeon, entity, direction, null);
	}
	
	public ActionKick(Dungeon dungeon, Entity entity, Integer[] direction, Entity kickedEntity) {
		super(dungeon, entity);
		
		this.direction = direction;
		this.kickedEntity = kickedEntity;
	}
	
	@Override
	public void execute() {
		runBeforeRunCallback();
		
		int dx = getEntity().getX() + direction[0];
		int dy = getEntity().getY() + direction[1];
		
		boolean isLivingEntity = getEntity() instanceof LivingEntity;
		
		if (!isLivingEntity) {
			return;
		}
		
		boolean isPlayer = getEntity() instanceof Player;
		LivingEntity entity = (LivingEntity) getEntity();
		
		if (kickedEntity != null) {
			entityKick(entity, isPlayer, dx, dy);
		} else {
			tileKick(entity, isPlayer, dx, dy);
		}
		
		runOnCompleteCallback();
	}
	
	private void entityKick(LivingEntity kicker, boolean isPlayer, int dx, int dy) {
		kickedEntity.kick(kicker, isPlayer, dx, dy);
	}
	
	private void tileKick(LivingEntity kicker, boolean isPlayer, int dx, int dy) {
		Tile tile = getEntity().getLevel().getTile(dx, dy);
		TileType tileType = getEntity().getLevel().getTileType(dx, dy);
		
		if (tileType == null || tileType.getSolidity() != TileType.Solidity.SOLID) {
			if (Utils.roll(5) == 1) {
				// TODO: If the player is skilled in martial arts or has high strength/agility, make them not strain their legs
				if (isPlayer) {
					getDungeon().logRandom(
						"[RED]Bad move! You strain your leg!",
						"[RED]Bad idea! You strain your leg!",
						"[RED]Ouch! You strain your leg!",
						"[RED]Crap! You strain your leg!",
						"[RED]Bad move! A sharp jolt shoots up your leg!",
						"[RED]Ouch! A sharp jolt shoots up your leg!"
					);
				}
				
				kicker.damage(DamageSource.KICKING_THIN_AIR, 1, kicker, isPlayer);
				kicker.addStatusEffect(new StrainedLeg(Utils.roll(3, 6)));
			} else {
				if (isPlayer) {
					getDungeon().You("kick thin air.");
				}
			}
			
			return;
		}
		
		if ((tileType == TileType.TILE_ROOM_DOOR_LOCKED || tileType == TileType.TILE_ROOM_DOOR_CLOSED) &&
			tile.hasState() &&
			tile.getState() instanceof TileStateDoor) {
			if (((TileStateDoor) tile.getState()).damage(1) > 0) { // TODO: Make this based on strength
				getDungeon().logRandom(
					"WHAMM!!",
					"CRASH!!"
				);
			} else {
				if (isPlayer) {
					getDungeon().logRandom(
						"The door crashes open!",
						"The door falls off its hinges!",
						"You kick the door off its hinges!",
						"You kick the door down!"
					);
				}
			}
		} else if (tileType.isWallTile()) {
			if (isPlayer) {
				getDungeon().You("kick the wall!");
			}
			
			if (Utils
				.roll(5) == 1) { // TODO: If the player is skilled in martial arts or has high strength/agility, make them not damage their feet
				if (isPlayer) {
					getDungeon().logRandom(
						"[RED]Ouch! That hurt a lot!",
						"[RED]Ouch! That caused some bad damage to your foot!"
					);
				}
				
				kicker.damage(DamageSource.KICKING_A_WALL, 1, kicker, isPlayer);
				kicker.addStatusEffect(new InjuredFoot(getDungeon(), kicker, Utils.roll(3, 6)));
			} else {
				if (isPlayer) {
					getDungeon().log("[ORANGE]Ouch! That hurt!");
				}
			}
		} else {
			if (isPlayer) {
				getDungeon().You("kick it!");
			}
		}
	}
}
