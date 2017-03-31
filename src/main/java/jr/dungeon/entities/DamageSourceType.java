package jr.dungeon.entities;

import lombok.Getter;

@Getter
public enum DamageSourceType {
	UNKNOWN("You die of an unknown cause.", "You died of an unknown cause."),
	DAGGER(DamageType.MELEE),
	SHORTSWORD(DamageType.MELEE),
	LONGSWORD(DamageType.MELEE),
	KICKING_A_WALL(DamageType.MELEE),
	KICKING_THIN_AIR(DamageType.MELEE),
	CANINE_BITE(DamageType.MELEE, "The bite pierces an artery. You bleed to death.", "The bite pierced an artery, and" +
		" you bled to death."),
	SPIDER_BITE(DamageType.MELEE, "The bite pierces an artery. You bleed to death.", "The bite pierced an artery, and" +
		" you bled to death."),
	RAT_BITE(DamageType.MELEE, "The bite pierces an artery. You bleed to death.", "The bite pierced an artery, and " +
		"you bled to death."),
	KICK_REVENGE(DamageType.MELEE),
	PLAYER_KICK(DamageType.MELEE),
	STAFF_BASH(DamageType.MELEE),
	SKELETON_HIT(DamageType.MELEE),
	GOBLIN_ZOMBIE_HIT(DamageType.MELEE),
	MOLD_RETALIATION(DamageType.MELEE),
	STRIKE_SPELL(DamageType.MAGIC, "The strike penetrates your body, cutting it into several pieces.", "The strike " +
		"penetrated your body, cutting it into several pieces."),
	FIRE("You burn to death.", "You burned to death."),
	POISON("The poison kills you.", "The poison killed you."),
	MERCURY("The mercury kills you.", "The mercury killed you."),
	FOOD_POISONING("It was a bad idea to eat that food. You die of food poisoning.", "It was a bad idea to eat that " +
		"food. You died of food poisoning."),
	CHOKING("You choke to death.", "You choked to death."),
	WISH_FOR_DEATH("You wish for death, and your god grants it.", "You wished for death, and your god granted it."),
	ARROW(DamageType.RANGED, "An arrow penetrates you. You die.", "An arrow penetrated you. You die.");
	
	DamageType damageType;
	String deathString, deathStringPastTense;
	
	DamageSourceType() {
		this.damageType = DamageType.OTHER;
	}
	
	DamageSourceType(DamageType damageType) {
		this.damageType = damageType;
	}
	
	DamageSourceType(String deathString, String deathStringPastTense) {
		this.damageType = DamageType.OTHER;
		this.deathString = deathString;
		this.deathStringPastTense = deathStringPastTense;
	}
	
	DamageSourceType(DamageType damageType, String deathString, String deathStringPastTense) {
		this.damageType = damageType;
		this.deathString = deathString;
		this.deathStringPastTense = deathStringPastTense;
	}
	
	public enum DamageType {
		MELEE, RANGED, MAGIC, OTHER
	}
}
