package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityWalkedOnEvent extends Event {
    private Entity walkedOn;
    private EntityLiving walker;
    
    public boolean isWalkedOnPlayer() {
        return walkedOn instanceof Player;
    }
    
    public boolean isWalkerPlayer() {
        return walker instanceof Player;
    }
    
    @Override
    public boolean isSelf(Object other) {
        return other.equals(walkedOn);
    }
}
