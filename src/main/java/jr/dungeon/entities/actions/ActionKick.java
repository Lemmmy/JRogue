package jr.dungeon.entities.actions;

import jr.dungeon.Messenger;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.InjuredFoot;
import jr.dungeon.entities.effects.StrainedLeg;
import jr.dungeon.entities.events.EntityKickedTileEvent;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.dungeon.tiles.states.TileStateDoor;
import jr.utils.RandomUtils;

/**
 * Kick action.
 *
 * @see jr.dungeon.entities.actions.EntityAction
 */
public class ActionKick extends EntityAction {
	private final Integer[] direction;
	private final Entity kickedEntity;
	
	/**
	 * Kick action. Explicitly kicks a tile.
	 *
	 * @param direction The direction to kick in, as a 2-element array as [dx, dy].
	 *                  For example, [1, 0] kicks to the right. [-1, -1] kicks north-west.
	 * @param callback Callback to call when action-related events occur. See
	 * {@link jr.dungeon.entities.actions.EntityAction.ActionCallback}.
	 */
	public ActionKick(Integer[] direction, ActionCallback callback) {
		// TODO: replace these silly Integer[] directions with arbitrary directions?
		
		this(direction, null, callback);
	}
	
	/**
	 * Kick action. Explicitly kicks an entity.
	 *
	 * @param direction The direction to kick in, as a 2-element array as [dx, dy].
	 *                  For example, [1, 0] kicks to the right. [-1, -1] kicks north-west.
	 * @param kicked The entity that was kicked.
	 * @param callback Callback to call when action-related events occur. See
	 * {@link jr.dungeon.entities.actions.EntityAction.ActionCallback}.
	 */
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
		
		kicker.getDungeon().triggerEvent(new EntityKickedTileEvent(kicker, tile));
	}
}
