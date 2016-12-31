package pw.lemmmy.jrogue.dungeon.entities.effects;

import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;

public class Mercury extends StatusEffect {
	public Mercury() {
		this(-1);
	}
	
	public Mercury(int duration) {
		super(duration);
	}
	
	@Override
	public void turn() {
		super.turn();
		
		if (getEntity() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) getEntity();
			
			if (
				livingEntity.getHealth() > 2 &&
				livingEntity.getHealth() < livingEntity.getMaxHealth() - 2 &&
				livingEntity.getDungeon().getTurn() % 5 == 0
			) {
				livingEntity.damage(getDamageSource(), 1, null, false);
			}
		}
	}
	
	@Override
	public String getName() {
		return "Mercury";
	}
	
	public DamageSource getDamageSource() {
		return DamageSource.MERCURY;
	}
	
	@Override
	public Severity getSeverity() {
		return Severity.CRITICAL;
	}
	
	@Override
	public void onEnd() {
		getMessenger().greenYou("managed to absorb the deadly mercury.");
	}
}
