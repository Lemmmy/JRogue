package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.events.DungeonEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FOR DEBUG PURPOSES ONLY
 */
@Getter
@AllArgsConstructor
public class EntityAttackedToHitRollEvent extends DungeonEvent {
	private Entity entity;
	private int x, y, roll, toHit;
}
