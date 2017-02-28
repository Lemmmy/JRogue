package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.events.DungeonEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityStatusEffectAddedEvent extends DungeonEvent {
	private Entity entity;
	private StatusEffect effect;
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(entity);
	}
}
