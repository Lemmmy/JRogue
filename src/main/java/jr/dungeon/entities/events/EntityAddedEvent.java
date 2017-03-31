package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityAddedEvent extends Event {
	private Entity entity;
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(entity);
	}
}
