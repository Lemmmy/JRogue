package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;

import java.util.List;

public class ItemCorpse extends ItemComestible {
	private LivingEntity entity;

	public ItemCorpse(LivingEntity entity) {
		super();

		this.entity = entity;
	}

	@Override
	public String getName(boolean requiresCapitalisation, boolean plural) {
		return (getEatenState() == EatenState.PARTLY_EATEN ? "partly eaten " : "") +
			entity.getName(requiresCapitalisation) +
			" corpse" + (plural ? "s" : "");
	}

	@Override
	public int getWeight() {
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
	public ItemCategory getCategory() {
		return ItemCategory.COMESTIBLE;
	}

	@Override
	public int getNutrition() {
		if (entity instanceof Monster) {
			return ((Monster) entity).getNutrition();
		} else {
			return 0;
		}
	}

	@Override
	public List<StatusEffect> getStatusEffects(LivingEntity victim) {
		if (entity instanceof Monster) {
			return ((Monster) entity).getCorpseEffects(victim);
		} else {
			return null;
		}
	}
}
