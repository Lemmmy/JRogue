package jr.dungeon.entities.interfaces;

import jr.dungeon.entities.EntityLiving;

public interface Readable {
	void read(EntityLiving reader);
	
	boolean canRead(EntityLiving reader);
}
