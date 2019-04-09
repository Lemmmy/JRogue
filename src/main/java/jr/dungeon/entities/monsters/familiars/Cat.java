package jr.dungeon.entities.monsters.familiars;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.monsters.ai.stateful.familiar.FamiliarAI;
import jr.dungeon.entities.monsters.ai.stateful.familiar.StateFollowPlayer;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.Verb;
import jr.utils.Point;
import jr.utils.RandomUtils;

@Wishable(name="cat")
@Registered(id="familiarCat")
public class Cat extends Familiar {
    public Cat(Dungeon dungeon, Level level, Point position) {
        super(dungeon, level, position);
        
        FamiliarAI ai = new FamiliarAI(this);
        setAI(ai);
        ai.setDefaultState(new StateFollowPlayer(ai, 0));
    }
    
    protected Cat() { super(); }
    
    @Override
    public Noun getDefaultName(EntityLiving observer) {
        return Lexicon.cat.clone();
    }
    
    @Override
    public int getWeight() {
        return 100 + getAge() * 50;
    }
    
    @Override
    public int getVisibilityRange() {
        return 15;
    }
    
    @Override
    public boolean canMoveDiagonally() {
        return true;
    }
    
    @Override
    public boolean canMeleeAttack() {
        return true;
    }
    
    @Override
    public boolean canRangedAttack() {
        return false;
    }
    
    @Override
    public boolean canMagicAttack() {
        return false;
    }
    
    @Override
    public int getBaseArmourClass() {
        return 6 - getAge();
    }
    
    @Override
    public Size getSize() {
        return Size.SMALL;
    }
    
    @Override
    public EntityAppearance getAppearance() {
        return EntityAppearance.APPEARANCE_TAMED_CAT;
    }
    
    @Override
    public DamageType getMeleeDamageType() {
        return DamageType.FELINE_BITE;
    }
    
    @Override
    public Verb getMeleeAttackVerb(EntityLiving victim) {
        return RandomUtils.randomFrom(Lexicon.swipe, Lexicon.bite).clone();
    }
}
