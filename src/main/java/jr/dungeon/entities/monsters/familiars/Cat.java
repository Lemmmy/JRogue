package jr.dungeon.entities.monsters.familiars;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;

public class Cat extends Familiar {
	public Cat(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	public Cat(Dungeon dungeon, Level level, int x, int y, int experienceLevel) {
		super(dungeon, level, x, y, experienceLevel);
	}
	
	@Override
	public int getWeight() {
		return 0;
	}
	
	@Override
	public int getVisibilityRange() {
		return 0;
	}
	
	@Override
	public boolean canMoveDiagonally() {
		return false;
	}
	
	@Override
	public boolean canMeleeAttack() {
		return false;
	}
	
	@Override
	public boolean canRangedAttack() {
		return false;
	}
	
	@Override
	public boolean canMagicAttack() {
		return false;
	}
	
	@Override
	public int getBaseArmourClass() {
		return 0;
	}
	
	@Override
	public Size getSize() {
		return null;
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return null;
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return null;
	}
}
