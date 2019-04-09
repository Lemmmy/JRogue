package jr.dungeon.entities.monsters.mold;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Point;

@Wishable(name="yellow mold")
@Registered(id="monsterMoldYellow")
public class MonsterMoldYellow extends MonsterMold {
    public MonsterMoldYellow(Dungeon dungeon, Level level, Point position) {
        super(dungeon, level, position);
    }
    
    protected MonsterMoldYellow() { super(); }
    
    @Override
    public Noun getName(EntityLiving observer) {
        return Lexicon.yellowMold.clone();
    }
    
    @Override
    public EntityAppearance getAppearance() {
        return EntityAppearance.APPEARANCE_MOLD_YELLOW;
    }
}
