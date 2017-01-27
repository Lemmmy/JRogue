package jr.dungeon.events;

import jr.dungeon.entities.Entity;

public class EntityAddedEvent extends DungeonEvent {
	private Entity entity;
	
	public EntityAddedEvent(Entity entity) {
		this.entity = entity;
	}
	
	public Entity getEntity() {
		return entity;
	}
}
