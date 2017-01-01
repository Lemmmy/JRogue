package pw.lemmmy.jrogue.dungeon.entities.effects;

import pw.lemmmy.jrogue.dungeon.entities.DamageSource;

public class MercuryPoisoning extends Poison {
	@Override
	public String getName() {
		return "Mercury Poisoning";
	}
	
	@Override
	public DamageSource getDamageSource() {
		return DamageSource.MERCURY;
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
