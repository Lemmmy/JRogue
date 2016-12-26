package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Hit;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;

public class ActionMelee extends EntityAction {
	private LivingEntity attacker;
	private LivingEntity victim;
	private DamageSource damageSource;
	private int damage;
	
	public ActionMelee(Dungeon dungeon,
					   LivingEntity attacker,
					   LivingEntity victim,
					   DamageSource damageSource,
					   int damage,
					   ActionCallback actionCallback) {
		super(dungeon, attacker, actionCallback);
		
		this.attacker = attacker;
		this.victim = victim;
		this.damageSource = damageSource;
		this.damage = damage;
	}
	
	@Override
	public void execute() {
		boolean isAttackerPlayer = attacker instanceof Player;
		boolean isVictimPlayer = victim instanceof Player;
		
		Hit hit = victim.doHit(damageSource, damage, attacker);
		
		switch (hit.getHitType()) {
			case JUST_MISS:
				if (isAttackerPlayer) {
					getDungeon().orangeYou("just miss the %s.", victim.getName(false));
				} else if (isVictimPlayer) {
					getDungeon().The("%s just misses.", attacker.getName(false));
				}
				break;
			case MISS:
				if (isAttackerPlayer) {
					getDungeon().orangeYou("miss the %s.", victim.getName(false));
				} else if (isVictimPlayer) {
					getDungeon().The("%s misses.", attacker.getName(false));
				}
				break;
			case SUCCESS:
				runBeforeRunCallback();
				victim.damage(damageSource, damage, attacker, isAttackerPlayer);
				runOnCompleteCallback();
				break;
		}
	}
}
