package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.DungeonEvent;

public class EntityKickedEvent extends DungeonEvent {
	private Entity victim;
	private EntityLiving kicker;
	private int dx, dy;
	
	public EntityKickedEvent(Entity victim, EntityLiving kicker, int dx, int dy) {
		this.victim = victim;
		this.kicker = kicker;
		this.dx = dx;
		this.dy = dy;
	}
	
	public Entity getVictim() {
		return victim;
	}
	
	public boolean isVictimPlayer() {
		return victim instanceof Player;
	}
	
	public EntityLiving getKicker() {
		return kicker;
	}
	
	public boolean isKickerPlayer() {
		return kicker instanceof Player;
	}
	
	public int getDeltaX() {
		return dx;
	}
	
	public int getDeltaY() {
		return dy;
	}
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(victim);
	}
}
