package jr.dungeon.items.quaffable.potions;

import jr.dungeon.entities.EntityLiving;

@FunctionalInterface
public interface PotionEffect {
	void apply(EntityLiving entity, float potency);
}
