package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.events.DungeonEvent;

/**
 * FOR DEBUG PURPOSES ONLY
 */
public class EntityAttackedToHitRollEvent extends DungeonEvent {
	private Entity entity;
	private int x, y, roll, toHit;
	
	public EntityAttackedToHitRollEvent(Entity entity, int x, int y, int roll, int toHit) {
		this.entity = entity;
		this.x = x;
		this.y = y;
		this.roll = roll;
		this.toHit = toHit;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getRoll() {
		return roll;
	}
	
	public int getToHit() {
		return toHit;
	}
}
