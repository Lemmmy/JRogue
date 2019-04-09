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

@Wishable(name="blue mold")
@Registered(id="monsterMoldBlue")
public class MonsterMoldBlue extends MonsterMold {
    public MonsterMoldBlue(Dungeon dungeon, Level level, Point position) {
        super(dungeon, level, position);
    }
    
    protected MonsterMoldBlue() { super(); }
    
    @Override
    public Noun getName(EntityLiving observer) {
        return Lexicon.blueMold.clone();
    }
    
    @Override
    public EntityAppearance getAppearance() {
        return EntityAppearance.APPEARANCE_MOLD_BLUE;
    }
}
