package jr.dungeon.entities;

import lombok.Getter;

@Getter
public enum DamageType {
    UNKNOWN(
        "You die of an unknown cause.",
        "You died of an unknown cause."
    ),
    DAGGER(DamageClass.MELEE),
    SHORTSWORD(DamageClass.MELEE),
    LONGSWORD(DamageClass.MELEE),
    KICKING_A_WALL(DamageClass.MELEE),
    KICKING_THIN_AIR(DamageClass.MELEE),
    CANINE_BITE(
        DamageClass.MELEE,
        "The bite pierces an artery. You bleed to death.",
        "The bite pierced an artery, and you bled to death."
    ),
    FELINE_BITE(
        DamageClass.MELEE,
        "The bite pierces an artery. You bleed to death.",
        "The bite pierced an artery, and you bled to death."
    ),
    LIZARD_BITE(
        DamageClass.MELEE,
        "The bite pierces an artery. You bleed to death.",
        "The bite pierced an artery, and you bled to death."
    ),
    SPIDER_BITE(
        DamageClass.MELEE,
        "The bite pierces an artery. You bleed to death.",
        "The bite pierced an artery, and you bled to death."
    ),
    RAT_BITE(
        DamageClass.MELEE,
        "The bite pierces an artery. You bleed to death.",
        "The bite pierced an artery, and you bled to death."
    ),
    KICK_REVENGE(DamageClass.MELEE),
    PLAYER_KICK(DamageClass.MELEE),
    STAFF_BASH(DamageClass.MELEE),
    SKELETON_HIT(DamageClass.MELEE),
    GOBLIN_HIT(DamageClass.MELEE),
    GOBLIN_ZOMBIE_HIT(DamageClass.MELEE),
    MOLD_RETALIATION(DamageClass.MELEE),
    STRIKE_SPELL(
        DamageClass.MAGIC,
        "The strike penetrates your body, cutting it into several pieces.",
        "The strike penetrated your body, cutting it into several pieces."
    ),
    FIRE(
        "You burn to death.",
        "You burned to death."
    ),
    POISON(
        "The poison kills you.",
        "The poison killed you."
    ),
    MERCURY(
        "The mercury kills you.",
        "The mercury killed you."
    ),
    FOOD_POISONING(
        "It was a bad idea to eat that food. You die of food poisoning.",
        "It was a bad idea to eat that food. You died of food poisoning."
    ),
    STARVING(
        "You starve to death.",
        "You starved to death."
    ),
    CHOKING(
        "You choke to death.",
        "You choked to death."
    ),
    WISH_FOR_DEATH(
        "You wish for death, and your god grants it.",
        "You wished for death, and your god granted it."
    ),
    ARROW(
        DamageClass.RANGED,
        "An arrow penetrates you. You die.",
        "An arrow penetrated you. You die."
    );
    
    DamageClass damageClass;
    String deathString, deathStringPastTense;
    
    DamageType() {
        this.damageClass = DamageClass.OTHER;
    }
    
    DamageType(DamageClass damageClass) {
        this.damageClass = damageClass;
    }
    
    DamageType(String deathString, String deathStringPastTense) {
        this.damageClass = DamageClass.OTHER;
        this.deathString = deathString;
        this.deathStringPastTense = deathStringPastTense;
    }
    
    DamageType(DamageClass damageClass, String deathString, String deathStringPastTense) {
        this.damageClass = damageClass;
        this.deathString = deathString;
        this.deathStringPastTense = deathStringPastTense;
    }
    
    public enum DamageClass {
        MELEE, RANGED, MAGIC, OTHER
    }
}
