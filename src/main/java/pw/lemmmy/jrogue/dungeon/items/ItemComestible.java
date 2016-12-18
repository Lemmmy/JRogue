package pw.lemmmy.jrogue.dungeon.items;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
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

	public abstract List<StatusEffect> getStatusEffects(LivingEntity victim);

	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);

		obj.put("eatenState", getEatenState().name());
	}

	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);

		eatenState = EatenState.valueOf(obj.getString("eatenState"));
	}

	public enum EatenState {
		UNEATEN,
		PARTLY_EATEN,
		EATEN
	}
}
