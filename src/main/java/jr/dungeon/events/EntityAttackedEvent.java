package jr.dungeon.events;

import jr.dungeon.entities.Entity;

public class EntityAttackedEvent extends DungeonEvent {
	private Entity entity;
	private int x, y, roll, toHit;
	
	public EntityAttackedEvent(Entity entity, int x, int y, int roll, int toHit) {
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
