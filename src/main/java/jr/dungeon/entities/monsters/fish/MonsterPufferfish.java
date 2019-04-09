package jr.dungeon.entities.monsters.fish;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.Poison;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityDamagedEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.FishAI;
import jr.dungeon.events.EventHandler;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Point;

import java.util.ArrayList;
import java.util.List;

@Wishable(name="pufferfish")
@Registered(id="monsterPufferfish")
public class MonsterPufferfish extends Monster {
    public MonsterPufferfish(Dungeon dungeon, Level level, Point position) {
        super(dungeon, level, position, 1);
        
        setAI(new FishAI(this));
    }
    
    protected MonsterPufferfish() { super(); }
    
    @Override
    public Noun getName(EntityLiving observer) {
        return Lexicon.pufferfish.clone();
    }
    
    @Override
    public EntityAppearance getAppearance() {
        return EntityAppearance.APPEARANCE_PUFFERFISH;
    }
    
    @Override
    public int getMovementSpeed() {
        return Dungeon.NORMAL_SPEED;
    }
    
    @Override
    public Size getSize() {
        return Size.SMALL;
    }
    
    @Override
    public boolean isHostile() {
        return false;
    }
    
    @Override
    public int getWeight() {
        return 50;
    }
    
    @Override
    public int getNutritionalValue() {
        return 50;
    }
    
    @Override
    public float getCorpseChance() {
        return 0.15f;
    }
    
    @Override
    public int getBaseArmourClass() {
        return 8;
    }
    
    @EventHandler(selfOnly = true)
    public void onDamage(EntityDamagedEvent e) {
        getDungeon().logRandom("Bloop.", "Glug.", "Splash!", "Sploosh!");
    }
    
    @Override
    public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
        List<StatusEffect> effects = new ArrayList<>();
        effects.add(new Poison());
        return effects;
    }
    
    @Override
    public int getVisibilityRange() {
        return 2;
    }
    
    @Override
    public boolean canMoveDiagonally() {
        return true;
    }
    
    @Override
    public boolean canMeleeAttack() {
        return false;
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
    public int getExperienceRewarded() {
        return 0;
    }
}
