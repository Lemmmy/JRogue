package jr.dungeon.entities.monsters.canines;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Colour;
import jr.utils.Point;

import java.util.List;

@Wishable(name="icehound")
@Registered(id="monsterIcehound")
public class MonsterIcehound extends MonsterHound implements LightEmitter {
    private static final Colour LIGHT_COLOUR = new Colour(0x8BD1ECFF);
    
    public MonsterIcehound(Dungeon dungeon, Level level, Point position) {
        super(dungeon, level, position);
    }
    
    protected MonsterIcehound() { super(); }
    
    @Override
    public Noun getName(EntityLiving observer) {
        return Lexicon.icehound.clone();
    }
    
    @Override
    public EntityAppearance getAppearance() {
        return EntityAppearance.APPEARANCE_ICEHOUND;
    }
    
    @Override
    public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
        return null; // TODO: Ice
    }
    
    @Override
    public Colour getLightColour() {
        return LIGHT_COLOUR;
    }
    
    @Override
    public int getLightIntensity() {
        return 60;
    }
}
