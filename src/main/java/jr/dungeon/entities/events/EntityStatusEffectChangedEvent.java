package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityStatusEffectChangedEvent extends Event {
    private Entity entity;
    private StatusEffect effect;
    
    private Change change;
    
    @Override
    public boolean isSelf(Object other) {
        return other.equals(entity);
    }
    
    public enum Change {
        ADDED, DURATION_CHANGED, REMOVED
    }
}
