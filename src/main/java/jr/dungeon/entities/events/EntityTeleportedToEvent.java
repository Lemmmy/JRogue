package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.DungeonEvent;

public class EntityTeleportedToEvent extends DungeonEvent {
	private Entity teleportedTo;
	private EntityLiving teleporter;
	
	public EntityTeleportedToEvent(Entity teleportedTo, EntityLiving walker) {
		this.teleportedTo = teleportedTo;
		this.teleporter = walker;
	}
	
	public Entity getTeleportedTo() {
		return teleportedTo;
	}
	
	public boolean isTeleportedToPlayer() {
		return teleportedTo instanceof Player;
	}
	
	public EntityLiving getTeleporter() {
		return teleporter;
	}
	
	public boolean isTeleporterPlayer() {
		return teleporter instanceof Player;
	}
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(teleportedTo);
	}
}
