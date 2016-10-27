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

	public ActionKick(Dungeon dungeon, Entity entity, Integer[] direction) {
		super(dungeon, entity);

		this.direction = direction;
	}

	@Override
	public void execute() {
		// TODO: Check for entities

		int dx = getEntity().getX() + direction[0];
		int dy = getEntity().getY() + direction[1];

		boolean isLivingEntity = getEntity() instanceof LivingEntity;

		if (!isLivingEntity) {
			return;
		}

		boolean isPlayer = getEntity() instanceof Player;
		LivingEntity entity = (LivingEntity) getEntity();

		if (isPlayer && entity.hasStatusEffect(InjuredFoot.class)) {
			getDungeon().Your("foot is in no shape for kicking.");
			return;
		}

		if (isPlayer && entity.hasStatusEffect(StrainedLeg.class)) {
			getDungeon().Your("leg is in no shape for kicking.");
			return;
		}

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

				entity.damage(DamageSource.KICKING_THIN_AIR, 1);
				entity.addStatusEffect(new StrainedLeg(getDungeon(), entity, Utils.roll(3, 6)));
			} else {
				if (isPlayer) {
					getDungeon().You("kick thin air.");
				}
			}

			return;
		}

		if (tile == TileType.TILE_ROOM_DOOR_CLOSED) {
			if (Utils.roll(6) == 1) {
				if (isPlayer) {
					getDungeon().logRandom(
						"The door crashes open!",
						"The door falls off its hinges!",
						"You kick the door off its hinges!",
						"You kick the door down!"
					);
				}

				entity.getLevel().setTile(dx, dy, TileType.TILE_ROOM_DOOR_BROKEN);
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

				entity.damage(DamageSource.KICKING_A_WALL, 1);
				entity.addStatusEffect(new InjuredFoot(getDungeon(), entity, Utils.roll(3, 6)));
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
