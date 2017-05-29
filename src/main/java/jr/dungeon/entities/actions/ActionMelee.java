package jr.dungeon.entities.actions;

import jr.dungeon.io.Messenger;
import jr.dungeon.entities.*;
import jr.dungeon.entities.events.EntityAttackMissedEvent;
import jr.dungeon.entities.player.Player;
import jr.language.LanguageUtils;
import jr.language.Lexicon;
import jr.language.transformers.Capitalise;

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
			case MISS:
				victim.getDungeon().eventSystem
					.triggerEvent(new EntityAttackMissedEvent(victim, damageSource, hit));
				
				msg.log(
					"%s%s %s%s %s.",
					isAttackerPlayer ? "[ORANGE]" : "",
					LanguageUtils.subject(attacker).build(Capitalise.first),
					hit.getHitType() == HitType.JUST_MISS ? "just " : "",
					LanguageUtils.autoTense(Lexicon.miss.clone(), attacker),
					LanguageUtils.object(victim)
				);
				
				break;
			case SUCCESS:
				runBeforeRunCallback(entity);
				victim.damage(damageSource, damage);
				runOnCompleteCallback(entity);
				break;
			default:
				break;
		}
	}
}
