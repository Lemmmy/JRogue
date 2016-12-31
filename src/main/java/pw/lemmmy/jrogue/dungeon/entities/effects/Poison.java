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
	
	@Override
	public void turn() {
		super.turn();
		
		if (getEntity() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) getEntity();
			
			livingEntity.damage(getDamageSource(), 1, null, false);
			
			if (getTurnsPassed() >= 15) {
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
