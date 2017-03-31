package jr.dungeon.entities.effects;

import jr.dungeon.entities.DamageSourceType;

public class MercuryPoisoning extends Poison {
	@Override
	public String getName() {
		return "Mercury Poisoning";
	}
	
	@Override
	public DamageSourceType getDamageSource() {
		return DamageSourceType.MERCURY;
	}
	
	@Override
	public int getDamageLimit() {
		return 2;
	}
	
	@Override
	public int getHealthLimit() {
		return 2;
	}
	
	@Override
	public Severity getSeverity() {
		return Severity.MAJOR;
	}
	
	@Override
	public void onEnd() {
		getMessenger().greenYou("are no longer suffering from mercury poisoning.");
	}
}
