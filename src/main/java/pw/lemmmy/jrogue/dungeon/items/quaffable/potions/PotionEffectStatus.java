package pw.lemmmy.jrogue.dungeon.items.quaffable.potions;

import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;

public class PotionEffectStatus implements PotionEffect {
	private final StatusEffect effect;
	
	public PotionEffectStatus(StatusEffect effect) {
		this.effect = effect;
	}
	
	@Override
	public void apply(EntityLiving entity, float potency) {
		entity.addStatusEffect(effect);
	}
}
