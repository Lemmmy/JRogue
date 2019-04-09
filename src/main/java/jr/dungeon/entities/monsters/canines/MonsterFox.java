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

@Wishable(name="fox")
@Registered(id="monsterFox")
public class MonsterFox extends MonsterCanine {
    public MonsterFox(Dungeon dungeon, Level level, Point position) {
        super(dungeon, level, position);
    }
    
    protected MonsterFox() { super(); }
    
    @Override
    public Noun getName(EntityLiving observer) {
        return Lexicon.fox.clone();
    }
    
    @Override
    public EntityAppearance getAppearance() {
        return EntityAppearance.APPEARANCE_FOX;
    }
    
    @Override
    public EntityLiving.Size getSize() {
        return EntityLiving.Size.SMALL;
    }
    
    @Override
    public int getMovementSpeed() {
        return Dungeon.NORMAL_SPEED + 3;
    }
    
    @Override
    public int getWeight() {
        return 250;
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
    public int getVisibilityRange() {
        return 15;
    }
}
