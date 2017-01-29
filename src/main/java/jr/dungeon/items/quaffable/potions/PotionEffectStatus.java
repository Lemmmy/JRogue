package jr.dungeon.items.quaffable.potions;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;

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
