package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.events.Event;
import jr.utils.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityMovedEvent extends Event {
	private Entity entity;
	private int lastX, lastY, newX, newY;
	
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
