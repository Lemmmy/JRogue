package pw.lemmmy.jrogue.dungeon.items.potions;

import com.badlogic.gdx.graphics.Color;
import pw.lemmmy.jrogue.dungeon.entities.effects.Poison;

public enum PotionType {
	POTION_HEALTH((ent, potency) -> ent.heal((int) Math.round(potency)));
	
	private final PotionEffect effect;
	
	PotionType(PotionEffect effect) {
		this.effect = effect;
	}
	
	public PotionEffect getEffect() {
		return effect;
	}
}
