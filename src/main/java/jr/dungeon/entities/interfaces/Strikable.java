package jr.dungeon.entities.interfaces;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.projectiles.EntityStrike;

public interface Strikable {
    void onStrike(EntityStrike strike, Entity source);
}
