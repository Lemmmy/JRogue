package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.DungeonEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityTeleportedToEvent extends DungeonEvent {
	private Entity teleportedTo;
	private EntityLiving teleporter;
	
	public boolean isTeleportedToPlayer() {
		return teleportedTo instanceof Player;
	}
	
	public boolean isTeleporterPlayer() {
		return teleporter instanceof Player;
	}
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(teleportedTo);
	}
}
