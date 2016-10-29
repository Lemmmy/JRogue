package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.TileType;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.dungeon.entities.effects.InjuredFoot;
import pw.lemmmy.jrogue.dungeon.entities.effects.StrainedLeg;
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
	}

	private void entityKick(LivingEntity kicker, boolean isPlayer, int dx, int dy) {
		kickedEntity.kick(kicker, isPlayer, dx, dy);
	}

	private void tileKick(LivingEntity kicker, boolean isPlayer, int dx, int dy) {
		TileType tile = getEntity().getLevel().getTile(dx, dy);

		if (tile == null || tile.getSolidity() != TileType.Solidity.SOLID) {
			if (Utils.roll(5) == 1) {
				if (isPlayer) {
					getDungeon().logRandom(
						"Bad move! You strain your leg!",
						"Bad idea! You strain your leg!",
						"Ouch! You strain your leg!",
						"Crap! You strain your leg!",
						"Bad move! A sharp jolt shoots up your leg!",
						"Ouch! A sharp jolt shoots up your leg!"
					);
				}

				kicker.damage(DamageSource.KICKING_THIN_AIR, 1, kicker, isPlayer);
				kicker.addStatusEffect(new StrainedLeg(getDungeon(), kicker, Utils.roll(3, 6)));
			} else {
				if (isPlayer) {
					getDungeon().You("kick thin air.");
				}
			}

			return;
		}

		if (tile == TileType.TILE_ROOM_DOOR_CLOSED) {
			if (Utils.roll(5) == 1) {
				if (isPlayer) {
					getDungeon().logRandom(
						"The door crashes open!",
						"The door falls off its hinges!",
						"You kick the door off its hinges!",
						"You kick the door down!"
					);
				}

				kicker.getLevel().setTile(dx, dy, TileType.TILE_ROOM_DOOR_BROKEN);
			} else {
				getDungeon().logRandom(
					"WHAMM!!",
					"CRASH!!"
				);
			}
		} else if (tile.isWallTile()) {
			if (isPlayer) {
				getDungeon().You("kick the wall!");
			}

			if (Utils.roll(5) == 1) {
				if (isPlayer) {
					getDungeon().logRandom(
						"Ouch! That hurt a lot!",
						"Ouch! That caused some bad damage to your foot!"
					);
				}

				kicker.damage(DamageSource.KICKING_A_WALL, 1, kicker, isPlayer);
				kicker.addStatusEffect(new InjuredFoot(getDungeon(), kicker, Utils.roll(3, 6)));
			} else {
				if (isPlayer) {
					getDungeon().log("Ouch! That hurt!");
				}
			}
		} else {
			if (isPlayer) {
				getDungeon().You("kick it!");
			}
		}
	}
}
