package jr.dungeon.entities.decoration;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.interfaces.Decorative;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Point;

@Wishable(name="stalagmites")
@Registered(id="stalagmites")
public class EntityStalagmites extends Entity implements Decorative {
    public EntityStalagmites(Dungeon dungeon, Level level, Point position) {
        super(dungeon, level, position);
    }
    
    @Override
    public Noun getName(EntityLiving observer) {
        return Lexicon.stalagmite;
    }
    
    @Override
    public EntityAppearance getAppearance() {
        return EntityAppearance.APPEARANCE_STALAGMITES;
    }
    
    @Override
    public boolean canBeWalkedOn() {
        return true;
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
}
