package jr.dungeon.items.quaffable.potions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PotionType {
	POTION_HEALTH((ent, potency) -> ent.heal((int) Math.round(potency)));
	
	private final PotionEffect effect;
}
