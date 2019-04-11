package jr.dungeon.entities.monsters.familiars;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.BeforePlayerChangeLevelEvent;
import jr.dungeon.entities.events.EntityDeathEvent;
import jr.dungeon.entities.interfaces.Friendly;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.player.NutritionState;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventPriority;
import jr.language.Noun;
import jr.language.transformers.Capitalise;
import jr.language.transformers.Possessive;
import jr.utils.Point;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * An {@link jr.dungeon.entities.Entity} tamed by the {@link jr.dungeon.entities.player.Player}. A Player's companion
 * in the dungeon.
 */
public abstract class Familiar extends Monster implements Friendly {
    /**
     * The familiar's age from 0 to 2. 0 is youngest, 2 is oldest.
     */
    @Expose @Getter private int age;
    
    @Expose private String name;
    
    @Expose @Getter @Setter private float nutrition = 1400;
    @Getter private NutritionState lastNutritionState;
    
    public Familiar(Dungeon dungeon, Level level, Point position) {
        super(dungeon, level, position);
    }
    
    protected Familiar() { super(); }
    
    @Override
    public int getNutritionalValue() {
        return getWeight();
    }
    
    public NutritionState getNutritionState() {
        return NutritionState.fromNutrition(nutrition);
    }
    
    @Override
    public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
        return null;
    }
    
    @Override
    public float getCorpseChance() {
        return 0; // what the hell
    }
    
    @Override
    public int getExperienceRewarded() {
        return 0;
    }
    
    @Override
    public boolean isHostile() {
        return false;
    }
    
    @Override
    public boolean canBeWalkedOn() {
        return true; // why did i write this method
    }
    
    public abstract Noun getDefaultName(EntityLiving observer);
    
    @Override
    public Noun getName(EntityLiving observer) {
        if (name != null) {
            return new Noun(name).addTransformer(Capitalise.class, Capitalise.first);
        } else if (observer instanceof Player) {
            return getDefaultName(observer)
                .addInstanceTransformer(Possessive.class, Possessive.your);
        } else {
            return getDefaultName(observer)
                .addInstanceTransformer(Possessive.class, Possessive.build(
                    getDungeon().getPlayer().getName(observer).build(Capitalise.first)
                ));
        }
    }
    
    @Override
    public void update() {
        super.update();
        
        updateNutrition();
    }
    
    private void updateNutrition() {
        if (getNutritionState() != lastNutritionState) {
            lastNutritionState = getNutritionState();
        }
        
        if (getNutritionState() == NutritionState.CHOKING) {
            damage(new DamageSource(this, null, DamageType.CHOKING), 1);
        }
        
        if (getNutritionState() == NutritionState.STARVING) {
            damage(new DamageSource(this, null, DamageType.STARVING), 1);
        }
        
        nutrition -= getNutritionLoss();
    }
    
    protected float getNutritionLoss() {
        return 1f / (2f + getSize().ordinal() * 2f);
    }
    
    @EventHandler
    public void onPlayerChangeLevel(BeforePlayerChangeLevelEvent e) {
        if (
            e.getSrc().getLevel().equals(getLevel()) &&
            getAI().distanceFromPlayer() <= 3
        ) {
            removeAction();
            setLevel(e.getDest().getLevel(), e.getDest().position);
        }
    }
    
    @EventHandler(selfOnly = true, priority = EventPriority.LOWEST)
    public void onDie(EntityDeathEvent e) {
        getDungeon().You("feel sad for a moment...");
        
        if (e.isAttackerPlayer()) {
            // TODO: cripple player's luck and god relationship. they are a terrible person
        }
    }
    
    @Override
    public ToStringBuilder toStringBuilder() {
        return super.toStringBuilder()
            .append("nutrition", nutrition)
            .append("age", age);
    }
}
