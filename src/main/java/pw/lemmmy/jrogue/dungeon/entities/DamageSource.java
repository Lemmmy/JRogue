package pw.lemmmy.jrogue.dungeon.entities;

public enum DamageSource {
	UNKNOWN("You die of an unknown cause."),
	DAGGER(DamageType.MELEE),
	SHORTSWORD(DamageType.MELEE),
	LONGSWORD(DamageType.MELEE),
	KICKING_A_WALL(DamageType.MELEE),
	KICKING_THIN_AIR(DamageType.MELEE),
	CANINE_BITE(DamageType.MELEE, "The bite pierces an artery. You bleed to death."),
	SPIDER_BITE(DamageType.MELEE, "The bite pierces an artery. You bleed to death."),
	RAT_BITE(DamageType.MELEE, "The bite pierces an artery. You bleed to death."),
	KICK_REVENGE(DamageType.MELEE),
	PLAYER_KICK(DamageType.MELEE),
	STAFF_BASH(DamageType.MELEE),
	SKELETON_HIT(DamageType.MELEE),
	POISON("The poison kills you."),
	MERCURY("The mercury kills you."),
	CHOKING("You choke to death."),
	WISH_FOR_DEATH("You wish for death, and your god grants it.");
	
	DamageType damageType;
	String deathString;
	
	DamageSource() {
		this.damageType = DamageType.OTHER;
	}
	
	DamageSource(DamageType damageType) {
		this.damageType = damageType;
	}
	
	DamageSource(String deathString) {
		this.damageType = DamageType.OTHER;
		this.deathString = deathString;
	}
	
	DamageSource(DamageType damageType, String deathString) {
		this.damageType = damageType;
		this.deathString = deathString;
	}
	
	public DamageType getDamageType() {
		return damageType;
	}
	
	public String getDeathString() {
		return deathString;
	}
	
	public enum DamageType {
		MELEE, RANGED, MAGIC, OTHER
	}
}
