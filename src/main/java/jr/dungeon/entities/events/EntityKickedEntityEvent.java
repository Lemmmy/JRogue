package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.DungeonEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityKickedEntityEvent extends DungeonEvent {
	private Entity victim;
	private EntityLiving kicker;
	
	@Getter(AccessLevel.NONE)
	private int dx, dy;
	
	public boolean isVictimPlayer() {
		return victim instanceof Player;
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
