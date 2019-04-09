package jr.dungeon.items;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.events.EventListener;
import jr.dungeon.items.identity.Aspect;
import jr.dungeon.items.identity.AspectBeatitude;
import jr.dungeon.serialisation.DungeonRegistries;
import jr.dungeon.serialisation.DungeonRegistry;
import jr.dungeon.serialisation.HasRegistry;
import jr.dungeon.serialisation.Serialisable;
import jr.language.Noun;
import jr.utils.DebugToStringStyle;
import jr.utils.RandomUtils;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@HasRegistry
public abstract class Item implements Serialisable, EventListener {
    @Expose private Map<String, Aspect> aspects = new HashMap<>();
    @Expose private Set<String> knownAspects = new HashSet<>();
    
    @Expose private int visualID;
    @Expose private int age;
    
    public Item() {
        this.visualID = RandomUtils.random(1000);
        addAspect(new AspectBeatitude());
    }
    
    public void update(Entity owner) {
        if (shouldAge()) {
            age++;
        }
    }
    
    public boolean shouldAge() {
        return true;
    }
    
    public abstract Noun getName(EntityLiving observer);
    
    public Noun getTransformedName(EntityLiving observer) {
        Noun name = getName(observer);
        applyNameTransformers(observer, name);
        return name;
    }
    
    public void applyNameTransformers(EntityLiving observer, Noun noun) {
        getAspects().entrySet().stream()
            .filter(e -> isAspectKnown(observer, e.getKey()))
            .map(Map.Entry::getValue)
            .filter(Objects::nonNull)
            .sorted(Comparator.comparingInt(Aspect::getNamePriority))
            .forEach(a -> a.applyNameTransformers(this, noun));
    }
    
    public abstract float getWeight();
    
    public boolean shouldStack() {
        return true;
    }
    
    public boolean equals(Item other) {
        return other.getClass() == getClass() &&
            other.getAppearance() == getAppearance() &&
            other.getAspects() == getAspects();
    }
    
    public static DungeonRegistry<Aspect> getAspectRegistry() {
        return DungeonRegistries.findRegistryForClass(Aspect.class)
            .orElseThrow(() -> new RuntimeException("Couldn't find Aspect registry in Item"));
    }
    
    public static String getAspectID(Class<? extends Aspect> aspectClass) {
        return getAspectRegistry().getID(aspectClass)
            .orElseThrow(() -> new RuntimeException(String.format("Couldn't find ID for Aspect `%s` in Item", aspectClass.getName())));
    }
    
    public List<Aspect> getPersistentAspects() {
        return aspects.values().stream().filter(Aspect::isPersistent).collect(Collectors.toList());
    }
    
    public Optional<Aspect> getAspect(Class<? extends Aspect> aspectClass) {
        return Optional.ofNullable(aspects.get(getAspectID(aspectClass)));
    }
    
    public boolean isAspectKnown(EntityLiving observer, Class<? extends Aspect> aspectClass) {
        return isAspectKnown(observer, getAspectID(aspectClass));
    }
    
    public boolean isAspectKnown(EntityLiving observer, String aspectID) {
        if (aspects.get(aspectID).isPersistent()) {
            return observer.isAspectKnown(this, aspectID);
        } else {
            return knownAspects.contains(aspectID);
        }
    }
    
    public void addAspect(Aspect aspect) {
        aspects.put(getAspectID(aspect.getClass()), aspect);
    }
    
    public void observeAspect(EntityLiving observer, Class<? extends Aspect> aspectClass) {
        observeAspect(observer, getAspectID(aspectClass));
    }
    
    public void observeAspect(EntityLiving observer, String aspectID) {
        if (!aspects.containsKey(aspectID)) {
            return; // can't observe an aspect that doesn't exist!!
        }
        
        if (aspects.get(aspectID).isPersistent()) {
            observer.observeAspect(this, aspectID);
        } else {
            knownAspects.add(aspectID);
        }
    }
    
    public abstract ItemAppearance getAppearance();
    
    public abstract ItemCategory getCategory();
    
    @Override
    public String toString() {
        return toStringBuilder().toString();
    }
    
    public ToStringBuilder toStringBuilder() {
        ToStringBuilder tsb = new ToStringBuilder(this, DebugToStringStyle.STYLE)
            .append("age", String.format("%,d (should age: %s)", age, shouldAge() ? "yes" : "no"))
            .append("visualID", visualID)
            .append("appearance", getAppearance().name().toLowerCase().replace("appearance_", ""))
            .append("category", getCategory().name().toLowerCase());
        
        getAspects().forEach((ac, a) -> tsb.append(a.toStringBuilder()));
        
        return tsb;
    }
}
