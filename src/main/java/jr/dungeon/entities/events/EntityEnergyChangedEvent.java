package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityEnergyChangedEvent extends Event {
    private Entity entity;
    private int oldEnergy, newEnergy;
    
    @Override
    public boolean isSelf(Object other) {
        return other.equals(entity);
    }
}
