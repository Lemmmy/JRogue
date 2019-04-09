package jr.dungeon.entities;

import lombok.Getter;

public class Hit {
    @Getter private final HitType hitType;
    @Getter private final int damage;
    
    public Hit(HitType hitType, int damage) {
        this.hitType = hitType;
        this.damage = damage;
    }
}
