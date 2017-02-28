package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.events.DungeonEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityEnergyChangedEvent extends DungeonEvent {
	private Entity entity;
	private int oldEnergy, newEnergy;
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(entity);
	}
}
