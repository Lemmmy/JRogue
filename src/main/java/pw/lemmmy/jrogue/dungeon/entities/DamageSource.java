package pw.lemmmy.jrogue.dungeon.entities;

public enum DamageSource {
	UNKNOWN(null),
	KICKING_A_WALL(null),
	KICKING_THIN_AIR(null),
	CANINE_BITE(null),
	KICK_REVENGE(null),
	PLAYER_KICK(null),
	STAFF_BASH(null),
	POISON("The poison kills you.");

	String deathString;

	DamageSource(String deathString) {
		this.deathString = deathString;
	}

	public String getDeathString() {
		return deathString;
	}
}
