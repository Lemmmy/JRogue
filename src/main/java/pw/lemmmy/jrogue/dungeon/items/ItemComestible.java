package pw.lemmmy.jrogue.dungeon.items;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;

import java.util.List;

public abstract class ItemComestible extends Item {
	private int turnsEaten = 0;
	
	public EatenState getEatenState() {
		if (turnsEaten == 0) {
			return EatenState.UNEATEN;
		} else if (turnsEaten >= getTurnsRequiredToEat()) {
			return EatenState.EATEN;
		} else {
			return EatenState.PARTLY_EATEN;
		}
	}
	
	public int getTurnsEaten() {
		return turnsEaten;
	}
	
	public int getTurnsRequiredToEat() {
		return 1;
	}
	
	public void eatPart() {
		turnsEaten++;
	}
	
	public List<StatusEffect> getStatusEffects(LivingEntity victim) {
		return null;
	}
	
	public abstract int getNutrition();
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.COMESTIBLE;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("turnsEaten", turnsEaten);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		turnsEaten = obj.getInt("turnsEaten");
	}
	
	@Override
	public boolean equals(Item other) {
		if (other instanceof ItemComestible) {
			ItemComestible otherComestible = (ItemComestible) other;
			
			if (getEatenState() != EatenState.UNEATEN || otherComestible.getEatenState() != EatenState.UNEATEN) {
				return false;
			}
		}
		
		return super.equals(other);
	}
	
	public enum EatenState {
		UNEATEN,
		PARTLY_EATEN,
		EATEN
	}
}
