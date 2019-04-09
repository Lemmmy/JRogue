package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityLevelledUpEvent extends Event {
    private Entity entity;
    private int newExperienceLevel;
    
    @Override
    public boolean isSelf(Object other) {
        return other.equals(entity);
    }
}
