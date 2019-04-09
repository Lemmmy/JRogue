package jr.dungeon.entities.monsters.canines;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Point;

import java.util.List;

@Wishable(name="hound")
@Registered(id="monsterHound")
public class MonsterHound extends MonsterCanine {
    public MonsterHound(Dungeon dungeon, Level level, Point position) {
        super(dungeon, level, position);
    }
    
    protected MonsterHound() { super(); }
    
    @Override
    public Noun getName(EntityLiving observer) {
        return Lexicon.hound.clone();
    }
    
    @Override
    public EntityAppearance getAppearance() {
        return EntityAppearance.APPEARANCE_HOUND;
    }
    
    @Override
    public Size getSize() {
        return Size.SMALL;
    }
    
    @Override
    public int getMovementSpeed() {
        return Dungeon.NORMAL_SPEED;
    }
    
    @Override
    public int getWeight() {
        return 300;
    }
    
    @Override
    public int getNutritionalValue() {
        return 200;
    }
    
    @Override
    public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
        return null;
    }
    
    @Override
    public int getBaseArmourClass() {
        return 5;
    }
    
    @Override
    public int getVisibilityRange() {
        return 15;
    }
}
