package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.events.DungeonEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityAddedEvent extends DungeonEvent {
	private Entity entity;
	
	/**
	 * Whether or not the entity was created, and not just spawned from a new level or unserialisation.
	 */
	private boolean isNew;
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(entity);
	}
}
