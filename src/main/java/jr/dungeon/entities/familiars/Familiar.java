package jr.dungeon.entities.familiars;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityLiving;

/**
 * An {@link jr.dungeon.entities.Entity} tamed by the {@link jr.dungeon.entities.player.Player}. A Player's companion
 * in the dungeon.
 */
public abstract class Familiar extends EntityLiving {
	public Familiar(Dungeon dungeon, Level level, int x, int y) { // unserialiastion constructor
		super(dungeon, level, x, y);
	}
	
	public Familiar(Dungeon dungeon, Level level, int x, int y, int experienceLevel) {
		super(dungeon, level, x, y, experienceLevel);
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true; // why did i write this method
	}
}
