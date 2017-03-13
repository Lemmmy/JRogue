package jr.dungeon.entities.actions;

import jr.dungeon.Messenger;
import jr.dungeon.entities.*;
import jr.dungeon.entities.events.EntityAttackMissedEvent;
import jr.dungeon.entities.player.Player;

/**
 * Melee attack action.
 *
 * @see Action
 */
public class ActionMelee extends Action {
	private final EntityLiving victim;
	private final DamageSource damageSource;
	private final int damage;
	
	/**
	 * Melee attack action.
	 *
	 * @param victim The entity that was attacked.
	 * @param damageSource The source of the damage. See {@link DamageSource}.
	 * @param damage The amount of damage to deal.
	 * @param callback Callback to call when action-related events occur. See
	 * {@link Action.ActionCallback}.
	 */
	public ActionMelee(EntityLiving victim, DamageSource damageSource, int damage, ActionCallback callback) {
		super(callback);
		this.victim = victim;
		this.damageSource = damageSource;
		this.damage = damage;
	}
	
	@Override
	public void execute(Entity entity, Messenger msg) {
		if (!(entity instanceof EntityLiving)) {
			return;
		}
		
		EntityLiving attacker = (EntityLiving) entity;
		boolean isAttackerPlayer = attacker instanceof Player;
		boolean isVictimPlayer = victim instanceof Player;
		
		Hit hit = new Hit(HitType.SUCCESS, damage);
		
		if (isAttackerPlayer && !isVictimPlayer) {
			hit = ((Player) attacker).hitAgainstMonster(damageSource, damage, victim);
		} else if (!isAttackerPlayer && isVictimPlayer) {
			hit = ((Player) victim).hitFromMonster(damageSource, damage, attacker);
		}
		
		switch (hit.getHitType()) {
			case JUST_MISS:
				victim.getDungeon().triggerEvent(new EntityAttackMissedEvent(victim, attacker, damageSource, hit));
				
				if (isAttackerPlayer) {
					msg.orangeYou("just miss the %s.", victim.getName((EntityLiving) entity, false));
				} else if (isVictimPlayer) {
					msg.The("%s just misses.", attacker.getName((EntityLiving) entity, false));
				}
				break;
			case MISS:
				victim.getDungeon().triggerEvent(new EntityAttackMissedEvent(victim, attacker, damageSource, hit));
				
				if (isAttackerPlayer) {
					msg.orangeYou("miss the %s.", victim.getName((EntityLiving) entity, false));
				} else if (isVictimPlayer) {
					msg.The("%s misses.", attacker.getName((EntityLiving) entity, false));
				}
				break;
			case SUCCESS:
				runBeforeRunCallback(entity);
				victim.damage(damageSource, damage, attacker);
				runOnCompleteCallback(entity);
				break;
			default:
				break;
		}
	}
}
