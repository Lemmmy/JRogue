package pw.lemmmy.jrogue.dungeon.entities.monsters.mold;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;

public class MonsterMoldYellow extends MonsterMold {
	public MonsterMoldYellow(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	public MonsterMoldYellow(Dungeon dungeon, Level level, int x, int y, int experienceLevel) {
		super(dungeon, level, x, y, experienceLevel);
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Yellow mold" : "yellow mold";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_MOLD_YELLOW;
	}
}
