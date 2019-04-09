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

@Wishable(name="green mold")
@Registered(id="monsterMoldGreen")
public class MonsterMoldGreen extends MonsterMold {
    public MonsterMoldGreen(Dungeon dungeon, Level level, Point position) {
        super(dungeon, level, position);
    }
    
    protected MonsterMoldGreen() { super(); }
    
    @Override
    public Noun getName(EntityLiving observer) {
        return Lexicon.greenMold.clone();
    }
    
    @Override
    public EntityAppearance getAppearance() {
        return EntityAppearance.APPEARANCE_MOLD_GREEN;
    }
}
