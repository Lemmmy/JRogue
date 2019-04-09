package jr.dungeon.entities.interfaces;

import jr.dungeon.entities.EntityLiving;

public interface Quaffable {
    void quaff(EntityLiving quaffer);
    
    boolean canQuaff(EntityLiving quaffer);
    
    String getQuaffConfirmationMessage(EntityLiving quaffer);
}
