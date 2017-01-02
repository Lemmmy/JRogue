package pw.lemmmy.jrogue.dungeon.entities.effects;

import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;

public class Poison extends StatusEffect {
	public Poison() {
		this(-1);
	}
	
	public Poison(int duration) {
		super(duration);
	}
	
	public int getDamageLimit() {
		return 0;
	}
	
	public int getHealthLimit() {
		return 0;
	}
	
	@Override
	public void turn() {
		super.turn();
		
		if (getEntity() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) getEntity();
			
			if (
				(getDamageLimit() == 0 ||
				 livingEntity.getHealth() > livingEntity.getMaxHealth() - getDamageLimit()) &&
				getHealthLimit() < livingEntity.getHealth()
			) {
				livingEntity.damage(getDamageSource(), 1, null, false);
			}
			
			if (getHealthLimit() >= livingEntity.getMaxHealth()) {
				// The victim is far too weak to take normal damage and is killed instantaneously
				livingEntity.kill(getDamageSource(), 1, null, false);
			}
		}
	}
	
	public DamageSource getDamageSource() {
		return DamageSource.POISON;
	}
	
	@Override
	public String getName() {
		return "Poison";
	}
	
	@Override
	public Severity getSeverity() {
		return Severity.CRITICAL;
	}
	
	@Override
	public void onEnd() {
		getMessenger().greenYou("managed to absorb the deadly poison.");
	}
}
