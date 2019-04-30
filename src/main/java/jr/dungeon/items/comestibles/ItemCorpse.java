package jr.dungeon.items.comestibles;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.FoodPoisoning;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.items.identity.AspectBeatitude;
import jr.dungeon.items.identity.AspectEatenState;
import jr.dungeon.items.identity.AspectRottenness;
import jr.dungeon.serialisation.Registered;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.transformers.TransformerType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Registered(id="itemCorpse")
public class ItemCorpse extends ItemComestible {
    @Expose private EntityLiving entity;
    
    public ItemCorpse() { // deserialisation constructor
        super();
        
        addAspect(new AspectRottenness());
        addAspect(new AspectEatenState());
    }
    
    public ItemCorpse(EntityLiving entity) {
        super();
        
        this.entity = entity;
        
        addAspect(new AspectRottenness());
        addAspect(new AspectEatenState());
    }
    
    @Override
    public void update(Entity owner) {
        super.update(owner);
        
        if (
            owner instanceof Player &&
            !isAspectKnown((EntityLiving) owner, AspectRottenness.class) &&
            getRottenness() > 7
        ) {
            observeAspect((EntityLiving) owner, AspectRottenness.class);
            
            owner.getDungeon().log("Something in your inventory really stinks...");
        }
    }
    
    @Override
    public Noun getBaseName(EntityLiving observer) {
        observeAspect(observer, AspectEatenState.class);
        
        return Lexicon.corpse.clone()
            .addInstanceTransformer(CorpseTransformer.class, (s, m) -> entity.getName(observer) + " " + s);
    }
    
    @Override
    public float getWeight() {
        if (entity instanceof Monster) {
            return ((Monster) entity).getWeight();
        } else {
            return 250;
        }
    }
    
    @Override
    public ItemAppearance getAppearance() {
        return ItemAppearance.APPEARANCE_CORPSE;
    }
    
    @Override
    public int getNutrition() {
        if (entity instanceof Monster) {
            return ((Monster) entity).getNutritionalValue();
        } else {
            return 0;
        }
    }
    
    @Override
    public int getTurnsRequiredToEat() {
        if (entity instanceof Monster) {
            return ((Monster) entity).getWeight() / 64 + 3;
        } else {
            return entity.getSize() == EntityLiving.Size.LARGE ? 5 : 4;
        }
    }
    
    @Override
    public List<StatusEffect> getStatusEffects(EntityLiving victim) {
        List<StatusEffect> effects = new ArrayList<>();
        
        if (entity instanceof Monster) {
            Monster monster = (Monster) entity;
            
            if (monster.getCorpseEffects(victim) != null) {
                effects.addAll(monster.getCorpseEffects(victim));
            }
            
            if (getRottenness() > 7) {
                effects.add(new FoodPoisoning(entity.getDungeon(), entity, this));
            }
        }
        
        return effects;
    }
    
    public int getRottenness() {
        if (entity instanceof Monster) {
            Monster monster = (Monster) entity;
            
            if (monster.shouldCorpsesRot()) {
                AtomicInteger rottenness = new AtomicInteger(getAge() / 15);
                
                getAspect(AspectBeatitude.class).ifPresent(a -> {
                    AspectBeatitude ab = (AspectBeatitude) a;
                    
                    switch (ab.getBeatitude()) {
                        case BLESSED:
                            rottenness.addAndGet(-2);
                            break;
                        case CURSED:
                            rottenness.addAndGet(2);
                            break;
                    }
                });
                
                return Math.max(rottenness.get(), 0);
            }
        }
        
        return 0;
    }
    
    @Override
    public boolean shouldStack() {
        return false;
    }
    
    @Override
    public boolean equals(Item other) {
        if (other instanceof ItemCorpse) {
            return super.equals(other) && ((ItemCorpse) other).getEntity().getClass() == entity.getClass();
        }
        
        return super.equals(other);
    }
    
    public class CorpseTransformer implements TransformerType {}
}
