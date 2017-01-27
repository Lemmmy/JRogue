package jr.dungeon.events;

import jr.dungeon.entities.Entity;

public class EntityRemovedEvent extends DungeonEvent {
	private Entity entity;
	
	public EntityRemovedEvent(Entity entity) {
		this.entity = entity;
	}
	
	public Entity getEntity() {
		return entity;
	}
}
