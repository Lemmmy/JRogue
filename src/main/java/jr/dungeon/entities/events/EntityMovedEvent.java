package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.events.Event;
import jr.utils.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class EntityMovedEvent extends Event {
	private Entity entity;
	private int lastX, lastY, newX, newY, deltaX, deltaY;
	
	public EntityMovedEvent(Entity entity, int lastX, int lastY, int newX, int newY) {
		this.entity = entity;
		this.lastX = lastX;
		this.lastY = lastY;
		this.newX = newX;
		this.newY = newY;
		this.deltaX = newX - lastX;
		this.deltaY = newY - lastY;
	}
	
	public Point getLastPosition() {
		return Point.getPoint(lastX, lastY);
	}
	
	public Point getNewPosition() {
		return Point.getPoint(newX, newY);
	}
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(entity);
	}
}
