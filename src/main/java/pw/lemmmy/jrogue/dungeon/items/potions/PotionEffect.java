package pw.lemmmy.jrogue.dungeon.items.potions;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;

@FunctionalInterface
public interface PotionEffect {
    void apply(LivingEntity entity, float potency);
}
