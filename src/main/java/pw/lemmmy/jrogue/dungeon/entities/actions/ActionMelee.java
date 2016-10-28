package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.Player;

public class ActionMelee extends EntityAction {
	private LivingEntity victim;
	private DamageSource damageSource;
	private int damage;

	public ActionMelee(Dungeon dungeon, LivingEntity attacker, LivingEntity victim, DamageSource damageSource, int damage, ActionCallback actionCallback) {
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
		LivingEntity attacker = (LivingEntity) getEntity();

		victim.damage(damageSource, damage, attacker, isAttackerPlayer);

		runCallback();
	}
}
