package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.events.Event;
import jr.utils.Point;
import jr.utils.VectorInt;
import lombok.Getter;

@Getter
public class EntityMovedEvent extends Event {
    private Entity entity;
    private Point lastPosition, newPosition;
    private VectorInt delta;
    
    public EntityMovedEvent(Entity entity, Point lastPosition, Point newPosition) {
        this.entity = entity;
        
        this.lastPosition = lastPosition;
        this.newPosition = newPosition;
        
        this.delta = VectorInt.between(lastPosition, newPosition);
    }
    
    @Override
    public boolean isSelf(Object other) {
        return other.equals(entity);
    }
}
