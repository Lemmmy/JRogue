package pw.lemmmy.jrogue.dungeon.entities;

public enum DamageSource {
	UNKNOWN("You die of an unknown cause."),
	DAGGER(),
	SHORTSWORD(),
	LONGSWORD(),
	KICKING_A_WALL(),
	KICKING_THIN_AIR(),
	CANINE_BITE("The bite pierces an artery. You bleed to death."),
	SPIDER_BITE("The bite pierces an artery. You bleed to death."),
	RAT_BITE("The bite pierces an artery. You bleed to death."),
	KICK_REVENGE(),
	PLAYER_KICK(),
	STAFF_BASH(),
	SKELETON_HIT(),
	POISON("The poison kills you."),
	CHOKING("You choke to death."),
	WISH_FOR_DEATH("You wish for death, and your god grants it.");
	
	String deathString;
	
	DamageSource() {}
	
	DamageSource(String deathString) {
		this.deathString = deathString;
	}
	
	public String getDeathString() {
		return deathString;
	}
}
