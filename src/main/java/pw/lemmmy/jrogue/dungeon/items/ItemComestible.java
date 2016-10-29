package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;

import java.util.List;

public abstract class ItemComestible extends Item {
	private EatenState eatenState = EatenState.UNEATEN;

	public EatenState getEatenState() {
		return eatenState;
	}

	public void setEatenState(EatenState eatenState) {
		this.eatenState = eatenState;
	}

	public abstract int getNutrition();

	public abstract List<StatusEffect> getStatusEffects();

	public enum EatenState {
		UNEATEN,
		PARTLY_EATEN,
		EATEN
	}
}
