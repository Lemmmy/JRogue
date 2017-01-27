package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.events.DungeonEvent;

public class EntityRemovedEvent extends DungeonEvent {
	private Entity entity;
	
	public EntityRemovedEvent(Entity entity) {
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
