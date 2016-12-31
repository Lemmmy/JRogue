package pw.lemmmy.jrogue.dungeon.entities.effects;

import pw.lemmmy.jrogue.dungeon.entities.DamageSource;

public class Mercury extends Poison {
	@Override
	public String getName() {
		return "Mercury";
	}
	
	@Override
	public DamageSource getDamageSource() {
		return DamageSource.MERCURY;
	}
	
	@Override
	public void onEnd() {
		getMessenger().greenYou("managed to absorb the deadly mercury.");
	}
}
