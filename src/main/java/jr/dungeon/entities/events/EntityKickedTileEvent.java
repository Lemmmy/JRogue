package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.Event;
import jr.dungeon.tiles.Tile;
import jr.utils.VectorInt;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EntityKickedTileEvent extends Event {
    private Entity kicker;
    private Tile tile;
    private VectorInt direction;
    
    public boolean isKickerPlayer() {
        return kicker instanceof Player;
    }
    
    @Override
    public boolean isSelf(Object other) {
        return other.equals(kicker);
    }
}
