package jr.dungeon.entities.interfaces;

import jr.dungeon.entities.EntityLiving;

public interface ReadableEntity {
    void read(EntityLiving reader);
    
    boolean canRead(EntityLiving reader);
}
