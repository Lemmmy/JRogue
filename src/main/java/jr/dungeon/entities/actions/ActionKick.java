package jr.dungeon.entities.actions;

import jr.dungeon.Messenger;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.InjuredFoot;
import jr.dungeon.entities.effects.StrainedLeg;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.states.TileStateDoor;
import jr.utils.RandomUtils;

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
		
		int x = entity.getX() + direction[0];
		int y = entity.getY() + direction[1];
		
		boolean isLivingEntity = entity instanceof EntityLiving;
		
		if (!isLivingEntity) {
			return;
		}
		
		boolean isPlayer = entity instanceof Player;
		EntityLiving entityLiving = (EntityLiving) entity;
		
		if (kickedEntity != null) {
			entityKick(msg, entity, entityLiving, isPlayer, direction[0], direction[1]);
		} else {
			tileKick(msg, entity, entityLiving, isPlayer, x, y);
		}
		
		runOnCompleteCallback(entity);
	}
	
	private void entityKick(Messenger msg, Entity entity, EntityLiving kicker, boolean isPlayer, int dx, int dy) {
		if (kickedEntity.isStatic()) {
			if (isPlayer) {
				entity.getDungeon().You("kick the %s!", kickedEntity.getName(kicker, false));
			} else {
				entity.getDungeon().The("%s kicks the %s!", kicker.getName(kicker, false), kickedEntity.getName(kicker, false));
			}
		}
		
		kickedEntity.kick(kicker, dx, dy);
	}
	
	private void tileKick(Messenger msg, Entity entity, EntityLiving kicker, boolean isPlayer, int x, int y) {
		Tile tile = entity.getLevel().getTileStore().getTile(x, y);
		TileType tileType = entity.getLevel().getTileStore().getTileType(x, y);
		
		if (tileType == null || tileType.getSolidity() != TileType.Solidity.SOLID) {
			if (RandomUtils.roll(5) == 1) {
				if (isPlayer) {
					Player player = (Player) kicker;
					
					if (player.getAttributes().getAttribute(Attribute.STRENGTH) >= 12) {
						return;
					}
					
					msg.logRandom(
						"[RED]Bad move! You strain your leg!",
						"[RED]Bad idea! You strain your leg!",
						"[RED]Ouch! You strain your leg!",
						"[RED]Crap! You strain your leg!",
						"[RED]Bad move! A sharp jolt shoots up your leg!",
						"[RED]Ouch! A sharp jolt shoots up your leg!"
					);
				}
				
				kicker.damage(DamageSource.KICKING_THIN_AIR, 1, kicker);
				kicker.addStatusEffect(new StrainedLeg(RandomUtils.roll(3, 6)));
			} else {
				if (isPlayer) {
					entity.getDungeon().You("kick thin air.");
				}
			}
			
			return;
		}
		
		if (tileType.isDoorShut() && tile.hasState() && tile.getState() instanceof TileStateDoor) {
			int damage = 1;
			
			if (isPlayer) {
				Player player = (Player) kicker;
				int strength = player.getAttributes().getAttribute(Attribute.STRENGTH);
				damage = RandomUtils.roll((int) Math.ceil(strength / 8) + 1);
			}
			
			TileStateDoor doorState = (TileStateDoor) tile.getState();
			
			if (doorState.damage(damage) > 0) {
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
			
			if (RandomUtils.roll(5) == 1) {
				if (isPlayer) {
					Player player = (Player) kicker;
					
					if (player.getAttributes().getAttribute(Attribute.STRENGTH) >= 12) {
						return;
					}
					
					msg.logRandom(
						"[RED]Ouch! That hurt a lot!",
						"[RED]Ouch! That caused some bad damage to your foot!"
					);
				}
				
				kicker.damage(DamageSource.KICKING_A_WALL, 1, kicker);
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
