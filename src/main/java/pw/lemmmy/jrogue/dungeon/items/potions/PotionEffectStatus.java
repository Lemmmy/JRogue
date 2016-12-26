package pw.lemmmy.jrogue.dungeon.items.potions;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;

public class PotionEffectStatus implements PotionEffect {
    private final StatusEffect effect;

    public PotionEffectStatus(StatusEffect effect) {
        this.effect = effect;
    }

    @Override
    public void apply(LivingEntity entity, float potency) {
        entity.addStatusEffect(effect);
    }
}
