package jr.dungeon.entities.actions;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StrainedLeg;
import jr.dungeon.entities.events.EntityKickedTileEvent;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.dungeon.io.Messenger;
import jr.dungeon.tiles.Solidity;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.language.LanguageUtils;
import jr.language.Lexicon;
import jr.language.transformers.Capitalise;
import jr.utils.Point;
import jr.utils.RandomUtils;
import jr.utils.VectorInt;

/**
 * Kick action.
 *
 * @see Action
 */
public class ActionKick extends Action {
	private final VectorInt direction;
	private final Entity kickedEntity;
	
	/**
	 * Kick action. Explicitly kicks a tile.
	 *
	 * @param direction The direction to kick in, as a 2-element array as [dx, dy].
	 *                  For example, [1, 0] kicks to the right. [-1, -1] kicks north-west.
	 * @param callback {@link Action.ActionCallback Callback} to call when action-related events occur.
	 */
	public ActionKick(VectorInt direction, ActionCallback callback) {
		this(direction, null, callback);
	}
	
	/**
	 * Kick action. Explicitly kicks an entity.
	 *
	 * @param direction The direction to kick in, as a 2-element array as [dx, dy].
	 *                  For example, [1, 0] kicks to the right. [-1, -1] kicks north-west.
	 * @param kicked The entity that was kicked.
	 * @param callback {@link Action.ActionCallback Callback} to call when action-related events occur.
	 */
	public ActionKick(VectorInt direction, Entity kicked, ActionCallback callback) {
		super(callback);
		this.direction = direction.normalised();
		this.kickedEntity = kicked;
	}
	
	@Override
	public void execute(Entity entity, Messenger msg) {
		runBeforeRunCallback(entity);
		
		boolean isLivingEntity = entity instanceof EntityLiving;
		if (!isLivingEntity) return;
		
		boolean isPlayer = entity instanceof Player;
		EntityLiving entityLiving = (EntityLiving) entity;
		
		if (kickedEntity != null) {
			entityKick(msg, entityLiving, isPlayer, direction);
		} else {
			Point kickPos = entity.getPosition().add(direction);
			tileKick(msg, entityLiving, isPlayer, direction, kickPos);
		}
		
		runOnCompleteCallback(entity);
	}
	
	private void entityKick(Messenger msg, EntityLiving kicker, boolean isPlayer, VectorInt direction) {
		if (kickedEntity.isStatic()) {
			kicker.getDungeon().log(
				"%s %s %s!",
				LanguageUtils.subject(kicker).build(Capitalise.first),
				LanguageUtils.autoTense(Lexicon.kick.clone(), kicker),
				LanguageUtils.object(kickedEntity)
			);
		}
		
		kickedEntity.kick(kicker, direction);
	}
	
	private void tileKick(Messenger msg, EntityLiving kicker, boolean isPlayer, VectorInt direction, Point position) {
		Tile tile = kicker.getLevel().tileStore.getTile(position);
		TileType tileType = kicker.getLevel().tileStore.getTileType(position);
		
		if (tileType == null || tileType.getSolidity() != Solidity.SOLID) {
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
				
				kicker.damage(new DamageSource(kicker, null, DamageType.KICKING_THIN_AIR), 1);
				kicker.addStatusEffect(new StrainedLeg(RandomUtils.roll(3, 6)));
			} else {
				if (isPlayer) {
					kicker.getDungeon().You("kick thin air.");
				}
			}
			
			return;
		}
		
		kicker.getDungeon().eventSystem.triggerEvent(new EntityKickedTileEvent(kicker, tile, direction));
	}
}
