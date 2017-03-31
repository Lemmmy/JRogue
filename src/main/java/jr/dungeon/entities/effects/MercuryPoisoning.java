package jr.dungeon.entities.effects;

import jr.dungeon.entities.DamageType;

public class MercuryPoisoning extends Poison {
	@Override
	public String getName() {
		return "Mercury Poisoning";
	}
	
	@Override
	public DamageType getDamageSourceType() {
		return DamageType.MERCURY;
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
