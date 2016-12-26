package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;

public class ActionMelee extends EntityAction {
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
		
		this.victim = victim;
		this.damageSource = damageSource;
		this.damage = damage;
	}
	
	@Override
	public void execute() {
		if (!(getEntity() instanceof LivingEntity)) {
			return;
		}
		
		boolean isAttackerPlayer = getEntity() instanceof Player;
		boolean isVictimPlayer = victim instanceof Player;
		LivingEntity attacker = (LivingEntity) getEntity();
		
		if (damage <= 0) {
			if (isAttackerPlayer) {
				getDungeon().log("You miss the %s.", victim.getName(false));
			} else if (isVictimPlayer) {
				getDungeon().log("The %s misses.", attacker.getName(false));
			}
		} else {
			runBeforeRunCallback();
			victim.damage(damageSource, damage, attacker, isAttackerPlayer);
		}
		
		runOnCompleteCallback();
	}
}
