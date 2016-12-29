package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Messenger;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.InjuredFoot;
import pw.lemmmy.jrogue.dungeon.entities.effects.StrainedLeg;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.dungeon.tiles.states.TileStateDoor;
import pw.lemmmy.jrogue.utils.RandomUtils;

public class ActionKick extends EntityAction {
	private final Integer[] direction;
	private final Entity kickedEntity;
	
	public ActionKick(Integer[] direction, ActionCallback callback) {
		this(direction, null, callback);
	}
	
	public ActionKick(Integer[] direction, Entity kicked, ActionCallback callback) {
		super(callback);
		this.direction = direction;
		this.kickedEntity = kicked;
	}
	
	@Override
	public void execute(Entity entity, Messenger msg) {
		runBeforeRunCallback(entity);
		
		int dx = entity.getX() + direction[0];
		int dy = entity.getY() + direction[1];
		
		boolean isLivingEntity = entity instanceof LivingEntity;
		
		if (!isLivingEntity) {
			return;
		}
		
		boolean isPlayer = entity instanceof Player;
		LivingEntity livingEntity = (LivingEntity) entity;
		
		if (kickedEntity != null) {
			entityKick(msg, entity, livingEntity, isPlayer, dx, dy);
		} else {
			tileKick(msg, entity, livingEntity, isPlayer, dx, dy);
		}
		
		runOnCompleteCallback(entity);
	}
	
	private void entityKick(Messenger msg, Entity entity, LivingEntity kicker, boolean isPlayer, int dx, int dy) {
		if (kickedEntity.isStatic()) {
			if (isPlayer) {
				entity.getDungeon().You("kick the %s!", kickedEntity.getName(false));
			} else {
				entity.getDungeon().The("%s kicks the %s!", kicker.getName(false), kickedEntity.getName(false));
			}
		}
    
		kickedEntity.kick(kicker, isPlayer, dx, dy);
	}
	
	private void tileKick(Messenger msg, Entity entity, LivingEntity kicker, boolean isPlayer, int dx, int dy) {
		Tile tile = entity.getLevel().getTile(dx, dy);
		TileType tileType = entity.getLevel().getTileType(dx, dy);
		
		if (tileType == null || tileType.getSolidity() != TileType.Solidity.SOLID) {
			if (RandomUtils.roll(5) == 1) {
				// TODO: If the player is skilled in martial arts or has high strength/agility, make them not strain their legs
				if (isPlayer) {
					msg.logRandom(
						"[RED]Bad move! You strain your leg!",
						"[RED]Bad idea! You strain your leg!",
						"[RED]Ouch! You strain your leg!",
						"[RED]Crap! You strain your leg!",
						"[RED]Bad move! A sharp jolt shoots up your leg!",
						"[RED]Ouch! A sharp jolt shoots up your leg!"
					);
				}
				
				kicker.damage(DamageSource.KICKING_THIN_AIR, 1, kicker, isPlayer);
				kicker.addStatusEffect(new StrainedLeg(RandomUtils.roll(3, 6)));
			} else {
				if (isPlayer) {
					entity.getDungeon().You("kick thin air.");
				}
			}
			
			return;
		}
		
		if ((tileType == TileType.TILE_ROOM_DOOR_LOCKED || tileType == TileType.TILE_ROOM_DOOR_CLOSED) &&
			tile.hasState() &&
			tile.getState() instanceof TileStateDoor) {
			if (((TileStateDoor) tile.getState()).damage(1) > 0) { // TODO: Make this based on strength
				msg.logRandom(
					"WHAMM!!",
					"CRASH!!"
				);
			} else {
				if (isPlayer) {
					msg.logRandom(
						"The door crashes open!",
						"The door falls off its hinges!",
						"You kick the door off its hinges!",
						"You kick the door down!"
					);
				}
			}
		} else if (tileType.isWallTile()) {
			if (isPlayer) {
				msg.You("kick the wall!");
			}
			
			if (RandomUtils
				.roll(5) == 1) { // TODO: If the player is skilled in martial arts or has high strength/agility, make them not damage their feet
				if (isPlayer) {
					msg.logRandom(
						"[RED]Ouch! That hurt a lot!",
						"[RED]Ouch! That caused some bad damage to your foot!"
					);
				}
				
				kicker.damage(DamageSource.KICKING_A_WALL, 1, kicker, isPlayer);
				kicker.addStatusEffect(new InjuredFoot(entity.getDungeon(), kicker, RandomUtils.roll(3, 6)));
			} else {
				if (isPlayer) {
					msg.log("[ORANGE]Ouch! That hurt!");
				}
			}
		} else {
			if (isPlayer) {
				msg.You("kick it!");
			}
		}
	}
}
