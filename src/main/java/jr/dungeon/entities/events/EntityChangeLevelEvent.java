package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.Event;
import jr.dungeon.tiles.Tile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityChangeLevelEvent extends Event {
    private Entity entity;
    private Tile src;
    private Tile dest;
    
    public boolean isEntityPlayer() {
        return entity instanceof Player;
    }
    
    @Override
    public boolean isSelf(Object other) {
        return other.equals(entity);
    }
}
