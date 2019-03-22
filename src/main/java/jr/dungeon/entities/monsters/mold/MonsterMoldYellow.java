package jr.dungeon.entities.monsters.mold;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.serialisation.Registered;
import jr.language.Lexicon;
import jr.language.Noun;

@Registered(id="monsterMoldYellow")
public class MonsterMoldYellow extends MonsterMold {
	public MonsterMoldYellow(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	public MonsterMoldYellow(Dungeon dungeon, Level level, int x, int y, int experienceLevel) {
		super(dungeon, level, x, y, experienceLevel);
	}
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.yellowMold.clone();
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_MOLD_YELLOW;
	}
}
