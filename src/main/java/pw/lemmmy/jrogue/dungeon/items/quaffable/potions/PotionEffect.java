package pw.lemmmy.jrogue.dungeon.items.quaffable.potions;

import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;

@FunctionalInterface
public interface PotionEffect {
	void apply(EntityLiving entity, float potency);
}
