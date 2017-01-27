package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.events.DungeonEvent;

public class EntityAddedEvent extends DungeonEvent {
	private Entity entity;
	
	public EntityAddedEvent(Entity entity) {
		this.entity = entity;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(entity);
	}
}
