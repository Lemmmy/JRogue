package jr.dungeon.entities.monsters.mold;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.serialisation.Registered;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Point;

@Registered(id="monsterMoldRed")
public class MonsterMoldRed extends MonsterMold {
	public MonsterMoldRed(Dungeon dungeon, Level level, Point position) {
		super(dungeon, level, position);
	}
	
	protected MonsterMoldRed() { super(); }
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.redMold.clone();
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_MOLD_RED;
	}
}
