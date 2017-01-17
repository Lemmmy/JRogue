package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Messenger;
import pw.lemmmy.jrogue.dungeon.entities.*;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;

public class ActionMelee extends EntityAction {
	private final EntityLiving victim;
	private final DamageSource damageSource;
	private final int damage;
	
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
				if (isAttackerPlayer) {
					msg.orangeYou("just miss the %s.", victim.getName((EntityLiving) entity, false));
				} else if (isVictimPlayer) {
					msg.The("%s just misses.", attacker.getName((EntityLiving) entity, false));
				}
				break;
			case MISS:
				if (isAttackerPlayer) {
					msg.orangeYou("miss the %s.", victim.getName((EntityLiving) entity, false));
				} else if (isVictimPlayer) {
					msg.The("%s misses.", attacker.getName((EntityLiving) entity, false));
				}
				break;
			case SUCCESS:
				runBeforeRunCallback(entity);
				victim.damage(damageSource, damage, attacker, isAttackerPlayer);
				runOnCompleteCallback(entity);
				break;
			default:
				break;
		}
	}
}
