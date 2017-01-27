package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.DungeonEvent;

public class EntityWalkedOnEvent extends DungeonEvent {
	private Entity walkedOn;
	private EntityLiving walker;
	
	public EntityWalkedOnEvent(Entity walkedOn, EntityLiving walker) {
		this.walkedOn = walkedOn;
		this.walker = walker;
	}
	
	public Entity getWalkedOn() {
		return walkedOn;
	}
	
	public boolean isWalkedOnPlayer() {
		return walkedOn instanceof Player;
	}
	
	public EntityLiving getWalker() {
		return walker;
	}
	
	public boolean isWalkerPlayer() {
		return walker instanceof Player;
	}
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(walkedOn);
	}
}
