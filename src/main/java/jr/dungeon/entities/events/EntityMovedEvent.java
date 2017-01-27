package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.events.DungeonEvent;

public class EntityMovedEvent extends DungeonEvent {
	private Entity entity;
	private int lastX, lastY, newX, newY;
	
	public EntityMovedEvent(Entity entity, int lastX, int lastY, int newX, int newY) {
		this.entity = entity;
		this.lastX = lastX;
		this.lastY = lastY;
		this.newX = newX;
		this.newY = newY;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public int getLastX() {
		return lastX;
	}
	
	public int getLastY() {
		return lastY;
	}
	
	public int getNewX() {
		return newX;
	}
	
	public int getNewY() {
		return newY;
	}
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(entity);
	}
}
