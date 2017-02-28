package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.events.DungeonEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityHealthChangedEvent extends DungeonEvent {
	private Entity entity;
	private int oldHealth, newHealth;
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(entity);
	}
}
